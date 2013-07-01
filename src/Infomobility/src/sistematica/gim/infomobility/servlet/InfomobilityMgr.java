package sistematica.gim.infomobility.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sistematica.gim.infomobility.conf.WebSettings;
import sistematica.gim.infomobility.utils.FileUtils;
import sistematica.gim.infomobility.utils.HeadingCalculator;
import sistematica.gim.infomobility.utils.Loader;
import sistematica.mobiworkdb.ds.DataSources;

/**
 * Servlet per la ricezione dei dati di Infomobility.
 * 
 * Infomobility invia i dati real-time a questa servlet tramite il metodo POST
 * del protocollo HTTP. I pacchetti di dati inviati sono di questo tipo:
 * 
 * 
 * 
 * 2011-10-28 14:35:30,b4992a0e745bccdaf1ddd0451f9ddd88316c0826,00001
 * 2011-10-28 14:10:23,10.97041,44.46655,1,1
 * 2011-10-28 14:10:25,10.97041,44.46655,0,1
 * 2011-10-28 14:10:27,10.97041,44.46654,1,1
 * 2011-10-28 14:10:29,10.9704,44.46654,1,1
 * ...
 * 
 * 
 * 
 * La prima riga del file e' un header, col timestamp del pacchetto, l'ID
 * traiettoria ed un sequenziale.
 * 
 * Le righe successive sono i singoli campionamenti FCD riferiti alla
 * traiettoria specificata (c'e' soltanto una traiettoria per singolo file!); i
 * campi di ciascun campionamento sono: - il timestamp - longitudine -
 * latitudine - velocità in Km/h - codice di tipo veicolo
 * 
 */
public class InfomobilityMgr extends HttpServlet
{
	public static final Logger log4j = Logger.getLogger(InfomobilityMgr.class);
	private SimpleDateFormat sdfToday = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log4j.info("Method doPost.");

		// // // // // // // // // STEP 1: read the HTTP POST payload

		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try
		{
			InputStream inputStream = request.getInputStream();
			if (inputStream != null)
			{
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0)
				{
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			}
			else
			{
				stringBuilder.append("");
			}
		}
		catch (IOException ex)
		{
			throw ex;
		}
		finally
		{
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException ex)
				{
					throw ex;
				}
			}
		}
		String body = stringBuilder.toString();
		body = URLDecoder.decode(body, "UTF-8");

		log4j.debug(body);

		// // // // // // // // // STEP 2: save data to DB

		Loader loader = null;
		Connection conn = null;
		int count = -1;
		InfomobilityFile file = null;

		try
		{
			file = parseFileFcd(body);
			conn = DataSources.getPools().getDefaultPool().getConnection();
			count = writeRawData(file.data, conn);

			if (count > 0)
				log4j.info("Inserted " + count + " new positions of Infomobility.it (SOURCE = " + WebSettings.FCD_SOURCE_NAME + ")");
			else
				log4j.warn("No positions inserted.");

			if (WebSettings.SENDMGR_ENABLE)
			{
				loader = Loader.getInstance();
				loader.addFile();
				loader.updatePositions(count);
			}
		}
		catch (Exception e)
		{
			log4j.error(e, e);
		}
		finally
		{
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException e)
				{
					log4j.error("Error while closing DB connection", e);
				}
			}
		}
		
		try
		{
			if (WebSettings.FCD_ARCHIVE_FILE_ENABLE)
			{
				String archiveDailyDir = WebSettings.FCD_ARCHIVE_FILE_DIR + File.separatorChar + getTodayDate();
				FileUtils.makeDir(archiveDailyDir);
				createFile(file.name, body, archiveDailyDir);
			}
		}
		catch (Exception e)
		{
			log4j.error("Error (probably it's about makeDir) while creating on-disk file", e);
		}

	}

	private void createFile(String nomeFile, String body, String path)
	{
		File file = null;
		FileOutputStream file_os = null;
		PrintStream output = null;

		try
		{
			file = new File(path + File.separator + nomeFile + ".txt");
			if (file.exists())
			{
				file = new File(path + File.separator + nomeFile + "_" + System.currentTimeMillis() + ".txt");
			}

			file_os = new FileOutputStream(file, false);
			output = new PrintStream(file_os);

			output.print(body);

			if (output != null)
			{
				output.flush();
				output.close();
			}

			if (file_os != null)
				file_os.close();

			log4j.info("Ok, " + file.getName() + " created.");

		}
		catch (Exception e)
		{
			log4j.error("Error while creating on-disk file", e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log4j.warn("Method doGet not implemented.");
	}

	public static InfomobilityFile parseFileFcd(String file) throws IOException
	{
		InfomobilityFile fileInfoMob = new InfomobilityFile();
		List<InfoMobRawData> listRawData = null;
		String nomeFile = "";

		String[] fileArr = null;
		String[] lineArr = null;

		String idVehicle = "";
		String latitude = "";
		String longitude = "";

		fileArr = file.split("\n");

		if (fileArr.length > 0)
		{
			listRawData = new ArrayList<InfoMobRawData>();
		}

		for (int i = 0; i < fileArr.length; i++)
		{
			log4j.trace(fileArr[i]);

			lineArr = fileArr[i].trim().split(",");

			if (i == 0)
			{
				idVehicle = lineArr[1];

				log4j.trace("---------------- HEADER -----------------");
				log4j.trace("      TimestampCreazione = " + lineArr[0]);
				log4j.trace("(IdTraiettoria)idVehicle = " + idVehicle);
				log4j.trace("             Sequenziale = " + lineArr[2]);
				log4j.trace("----------------- BODY -----------------");

				nomeFile = lineArr[1] + "_" + lineArr[2];
			}
			else
			{
				log4j.trace("---------------");
				log4j.trace("FCD #" + (i));
				log4j.trace("timestamp = " + lineArr[0]);
				log4j.trace(" latitude = " + lineArr[2]);
				log4j.trace("longitude = " + lineArr[1]);
				log4j.trace("    speed = " + lineArr[3]);
				log4j.trace("     type = " + lineArr[4]);

				// JCS - Jaguar Coordinate System
				latitude = String.format("%.0f", Double.parseDouble(lineArr[2]) * 1000000);
				longitude = String.format("%.0f", Double.parseDouble(lineArr[1]) * 1000000);

				listRawData.add(new InfoMobRawData(idVehicle, lineArr[0], null, null, lineArr[3], "-1", null, null, latitude, longitude, null, null, lineArr[4]));

			}
		}
		log4j.trace("--------------- END BODY -----------------");
		
		fileInfoMob.data = listRawData;
		fileInfoMob.name = nomeFile;
		
		return fileInfoMob;

	}

	public static int writeRawData(List<InfoMobRawData> listRawData, Connection conn) throws FileNotFoundException, IOException, SQLException
	{
		PreparedStatement ps = null;
		int res = 0;
		InfoMobRawData data = null;
		HeadingCalculator calc = new HeadingCalculator();

		// String query =
		// "insert into position_fcd (id_vehicle,speed,timestamp,latitude,longitude,dir,id_panelsession,quality,deltapos,deltatime) values (?,?,STR_TO_DATE(?,'%d-%c-%Y %H:%i:%s'),?,?,?,?,?,?,?)";
		String query = " insert into position_fcd "
				+ " (id_vehicle, timestamp, timestamp_gps, gps_status, dir, speed, satellites, odometer, latitude, longitude, `key`, event, vehicle_type, source) values "
				+ " (?, STR_TO_DATE(?,'%Y-%c-%d %H:%i:%s'), STR_TO_DATE(?,'%Y-%c-%d %H:%i:%s'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try
		{
			ps = conn.prepareStatement(query);

			for (int i = 0; i < listRawData.size(); i++)
			{
				data = listRawData.get(i);

				calc.addSample(data);

				ps.setString(1, data.getVehicleId());
				ps.setString(2, data.getTimestamp());

				if (data.getTimestampGps() != null && isDate(data.getTimestampGps()))
					ps.setString(3, data.getTimestampGps());
				else
					ps.setNull(3, java.sql.Types.VARCHAR);

				setVarchar(ps, 4, data.getGpsStaus());
				ps.setString(5, String.format("%.0f", calc.getHeading()));
				ps.setString(6, data.getSpeed());
				setVarchar(ps, 7, data.getSatellites());
				setVarchar(ps, 8, data.getOdometer());
				ps.setString(9, data.getLatitude());
				ps.setString(10, data.getLongitude());
				setVarchar(ps, 11, data.getKey());
				setVarchar(ps, 12, data.getEvent());
				setVarchar(ps, 13, data.getType());
				ps.setString(14, WebSettings.FCD_SOURCE_NAME);

				log4j.trace(ps.toString().substring(ps.toString().indexOf(":") + 1));

				ps.addBatch();
			}

			int resArr[] = ps.executeBatch();

			for (int i = 0; i < resArr.length; i++)
			{
				if (resArr[i] == Statement.SUCCESS_NO_INFO)
				{
					res++;
				}
				else if (resArr[i] >= 0)
				{
					res++;
				}
				else if (resArr[i] == Statement.EXECUTE_FAILED)
				{
					// Nothing to do
				}
			}

			if (ps != null)
				ps.close();
		}
		catch (Exception e)
		{
			log4j.error("Error while writing a FCD sample on DB", e);
		}

		return res;
	}

	private static void setVarchar(PreparedStatement ps, int index, String data) throws SQLException
	{
		if (data != null)
			ps.setString(index, data);
		else
			ps.setNull(index, java.sql.Types.VARCHAR);
	}

	private static boolean isDate(String date)
	{
		String time = "(\\s(([01]?\\d)|(2[0123]))[:](([012345]\\d)|(60))" + "[:](([012345]\\d)|(60)))?";

		String day = "(([12]\\d)|(3[01])|(0?[1-9]))"; // 01 up to 31
		String month = "((1[012])|(0\\d))"; // 01 up to 12
		String year = "\\d{4}";

		// define here all date format
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		patterns.add(Pattern.compile(day + "[-.]" + month + "[-.]" + year + time));
		patterns.add(Pattern.compile(year + "-" + month + "-" + day + time));
		// here you can add more date formats if you want

		// check dates
		for (Pattern p : patterns)
			if (p.matcher(date).matches())
				return true;

		return false;

	}
	
	private String getTodayDate()
	{
		String res = "";
		try
		{
			res = sdfToday.format(new Date(System.currentTimeMillis()));
		}
		catch(Exception e)
		{
			log4j.error("Error while creating today date as a String.", e);
		}
		
		return res;	
	}
}
