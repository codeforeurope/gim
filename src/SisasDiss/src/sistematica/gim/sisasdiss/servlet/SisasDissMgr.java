package sistematica.gim.sisasdiss.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sistematica.gim.sisasdiss.conf.WebSettings;
import sistematica.gim.sisasdiss.utils.Loader;
import sistematica.mobiworkdb.ds.DataSources;

/**
 * Servlet per la ricezione dei dati dei Dissuasori SISAS.
 * 
 * SISAS invia i dati real-time a questa servlet tramite il metodo POST
 * del protocollo HTTP. I pacchetti di dati inviati sono di questo tipo:
 * 
 * 
 * 
 * 2011-11-09 12:05:00,dissuasore001 
 * 2011-11-09 12:00:10,052,1,1
 * 2011-11-09 12:00:47,048,0,2
 * 2011-11-09 12:01:00,059,1,3
 * 2011-11-09 12:01:23,046,0,4
 * 2011-11-09 12:02:11,051,1,5
 * 
 * ...
 * 
 * 
 * La prima riga del file e' un header, col timestamp del pacchetto e l'ID del Dissuasore
 * 
 * 
 * Le righe successive sono i singoli campionamenti riferiti al Dissuasore 
 * specificata; i campi di ciascun campionamento sono: - il timestamp - velocità -
 * in Km/h - superamento velocità - contatore
 * 
 */
public class SisasDissMgr extends HttpServlet
{
	public static final Logger log4j = Logger.getLogger(SisasDissMgr.class);

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
		SisasDissFile file = null;

		try
		{
			file = parseFileSisasDiss(body);
			
			if(file != null)
			{
				calculateStatistics(file);
				log4j.trace("----- Speed Stats ------");
				log4j.trace("  velocitaMedia = " + file.velocitaMedia);
				log4j.trace("velocitaMassima = " + file.velocitaMassima);
				log4j.trace(" velocitaMinima = " + file.velocitaMinima);
			
				if(WebSettings.DB_WRITE_ENABLE)
				{
					conn = DataSources.getPools().getDefaultPool().getConnection();
					writeDatiAggr(file, conn);
				}
				
				if (WebSettings.SENDMGR_ENABLE)
				{
					loader = Loader.getInstance();
					loader.addFile();
					
					if(file.data != null)
						loader.updatePositions(file.data.size());
				}
			}
			else
			{
				log4j.error("Problem occurred parsing the file: file is null.");
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
				createFile(file.name, body, WebSettings.FCD_ARCHIVE_FILE_DIR);
			}
		}
		catch (Exception e)
		{
			log4j.error("Error while creating on-disk file", e);
		}

	}

	private void writeDatiAggr(SisasDissFile file, Connection conn)
	{
		PreparedStatement ps = null;
		long idAggregato = 1L;
		
		SisasDiss diss = getAnagraficaDissuasore(file.tagDissuasore, conn);

		if(diss != null)
		{
			/*
			 * QUERY DA FILE DI PROPERTIES:
			 * 
			 * 	insert INTO DatiAggregati 
			 * 		(DatiAggregati.IdAggregato, DatiAggregati.IdSezione, 
			 * 		DatiAggregati.DataOra, DatiAggregati.Periodicita, 
			 * 		DatiAggregati.IdCorsia, DatiAggregati.NumeroVeicoli, 
			 *		DatiAggregati.VelocitaMedia, DatiAggregati.VelocitaMassima,
			 *  	DatiAggregati.VelocitaMinima ) 
			 *  values 
			 *  (?,?,STR_TO_DATE(?,'%Y-%c-%d %H:%i:%s'),?,?,?,?,?,?);
			 */
			String query = WebSettings.DB_WRITE_SQLINSERT;
			try
			{
				ps = conn.prepareStatement(query);
				
				if(diss.maxIdAggregato != null && diss.maxIdAggregato.length() > 0)
					idAggregato = Long.parseLong(diss.maxIdAggregato);
				
				//idAggregato, sequence del dissuasore
				ps.setLong(1, (idAggregato+1));
				
				//id del dissuasore
				ps.setString(2, diss.id);
				
				//timestamp di creazione del file
				ps.setString(3, file.timestamp);
				
				//periodicità tipicamente ogni 5 minuti, da file di properties
				ps.setString(4, file.periodicita);
				
				//L'id corsia non è presente nel file, metto 1
				ps.setInt(5, 1);
				
				//Numero di posizioni nel file
				ps.setInt(6, file.data.size());
				
				//POSSONO ESSERE NULL
				setVarchar(ps, 7, String.valueOf(file.velocitaMedia));
				setVarchar(ps, 8, String.valueOf(file.velocitaMassima));
				setVarchar(ps, 9, String.valueOf(file.velocitaMinima));
				
				log4j.info(ps.toString());
				ps.execute();

				if(ps.getUpdateCount() == 0)
					log4j.warn("No DatiAggregati in the DB_MILANO inserted.");
				else
					log4j.info(ps.getUpdateCount() + " DatiAggregati in the DB_MILANO correctly inserted.");

	
				if (ps != null)
					ps.close();
			}
			catch (Exception e)
			{
				log4j.error("Error while writing a FCD sample on DB", e);
			}
		}
		else
		{
			log4j.warn("The DISSUASORE " + file.tagDissuasore + " is not registered into the DB, nothing to do.");
		}

	}

	private void calculateStatistics(SisasDissFile file)
	{
		file.numeroVeicoli = file.data.size();
		
		if(file.speedList != null && file.speedList.size() > 0)
		{
			file.velocitaMedia = calculateAvgSpeed(file.speedList);
			file.velocitaMassima = calculateMaxSpeed(file.speedList);
			file.velocitaMinima = calculateMinSpeed(file.speedList);
		}
		else
		{
			file.velocitaMedia = null;
			file.velocitaMassima = null;
			file.velocitaMinima = null;
		}
	}

	private Double calculateAvgSpeed(List<Double> speedList)
	{
		Double avg = 0d;
		
		for(Double speed : speedList)
		{
			avg += speed;
		}
		
		avg = avg/speedList.size();
		
		return avg;
	}

	private Double calculateMaxSpeed(List<Double> speedList)
	{
		Double max = 0d;
		
		for(Double speed : speedList)
		{
			if(speed > max)
				max = speed;
		}
		
		return max;
	}
	
	private Double calculateMinSpeed(List<Double> speedList)
	{
		Double min = Double.MAX_VALUE;
		
		for(Double speed : speedList)
		{
			if(speed < min)
				min = speed;
		}
		
		if(min == Double.MAX_VALUE)
		{
			min = 0d;
		}
		
		return min;
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

	public static SisasDissFile parseFileSisasDiss(String file) throws IOException
	{
		SisasDissFile fileSisasDiss = new SisasDissFile();
		List<Double> speedList = null;
		List<SisasDissData> listRawData = null;
		String nomeFile = "";
		
		String[] fileArr = null;
		String[] lineArr = null;

		String tagDissuasore = "";
		String timestamp = "";

		fileArr = file.split("\n");

		if (fileArr.length > 0)
		{
			listRawData = new ArrayList<SisasDissData>();
		}

		for (int i = 0; i < fileArr.length; i++)
		{
			
			lineArr = fileArr[i].trim().split(",");

			if (i == 0)
			{
				tagDissuasore = lineArr[1];
				timestamp = lineArr[0];
				

				log4j.trace("---------------- HEADER -----------------");
				log4j.trace(fileArr[i]);
				log4j.trace("  TimestampCreazione = " + timestamp);
				log4j.trace("       IdDissuasiore = " + tagDissuasore);
				log4j.trace("----------------- BODY -----------------");

				nomeFile = lineArr[0] + "_" + lineArr[1];
				nomeFile = nomeFile.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
				
			}
			else
			{
				log4j.trace("---------------");
				log4j.trace("FCD #" + (i));
				log4j.trace(fileArr[i]);
				log4j.trace("    timestamp = " + lineArr[0]);
				log4j.trace("        speed = " + lineArr[1]);
				log4j.trace("limitExceeded = " + lineArr[2]);
				log4j.trace("  progressive = " + lineArr[3]);

				listRawData.add(new SisasDissData(tagDissuasore, lineArr[0], lineArr[1], lineArr[2], lineArr[3]));
				
				if(speedList == null)
				{
					speedList = new ArrayList<Double>();
				}
				
				speedList.add(Double.parseDouble(lineArr[1]));

			}
		}
		log4j.trace("--------------- END BODY -----------------");
		
		fileSisasDiss.tagDissuasore = tagDissuasore;
		fileSisasDiss.timestamp = timestamp;
		fileSisasDiss.data = listRawData;
		fileSisasDiss.name = nomeFile;
		fileSisasDiss.speedList = speedList;
		fileSisasDiss.periodicita = WebSettings.DISS_PERIODICITA;
		
		return fileSisasDiss;
	}
	
	private static void setVarchar(PreparedStatement ps, int index, String data) throws SQLException
	{
		if (data != null)
			ps.setString(index, data);
		else
			ps.setNull(index, java.sql.Types.VARCHAR);
	}
	
	private static SisasDiss getAnagraficaDissuasore(String tagDissuasore, Connection conn)
	{
		SisasDiss dissuasore = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String	sql = " SELECT " + 
					  " Sezioni.IdSezione as ID, " +
					  " Sezioni.TagSezione as TAG, " +
					  " Sezioni.DescrSezione as DESCRIZIONE, " +
					  " Sezioni.X as LAT," +
					  " Sezioni.Y as LON, " +
					  " Sezioni.Direzione as HEADING," +
					  " MDA.MAX_ID_AGGR" +
					  " FROM Sezioni " +
					  " LEFT JOIN" +
					  " (SELECT idSezione, MAX(idAggregato) as MAX_ID_AGGR from DatiAggregati group by idSezione) MDA on MDA.idSezione = Sezioni.IdSezione" +
					  " WHERE Sezioni.TagSezione = ? ";
		
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setString(1, tagDissuasore);
			
			rs = ps.executeQuery();
			
			if(rs.next())
			{
				dissuasore = new SisasDiss(rs.getString("ID"), tagDissuasore, rs.getString("DESCRIZIONE"), rs.getString("LAT"), rs.getString("LON"), rs.getString("HEADING"), rs.getString("MAX_ID_AGGR"));
				
				log4j.trace("----DISSUASORE----");
				log4j.trace("            id = " + dissuasore.id);
				log4j.trace("           tag = " + dissuasore.tag);
				log4j.trace("          desc = " + dissuasore.desc);
				log4j.trace("           lat = " + dissuasore.lat);
				log4j.trace("           lon = " + dissuasore.lon);
				log4j.trace("       heading = " + dissuasore.heading);
				log4j.trace("maxIdAggregato = " + dissuasore.maxIdAggregato);
				log4j.trace("------------------");
				
			}
			
			
		}
		catch(Exception e)
		{
			log4j.error(e,e);
		}
		
		return dissuasore;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		log4j.warn("Method doGet not implemented.");
	}
}
