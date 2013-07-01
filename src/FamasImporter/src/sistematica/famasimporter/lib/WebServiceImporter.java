package sistematica.famasimporter.lib;



import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import sistematica.famasimporter.data.Aggregati;
import sistematica.famasimporter.data.DatiVeicolo;
import sistematica.famasimporter.settings.Keys;
import sistematica.famasimporter.utils.DbConnection;
import sistematica.famasimporter.utils.FileUtils;

/**
 * @author gsilvestri
 *
 */
public class WebServiceImporter extends Thread
{
	public Logger log4j = Logger.getLogger(WebServiceImporter.class);
	
	private String wsIp = null;
	private Integer wsPort = null;
	private String wsUrl = null;
	
	private boolean stop = false;
	
	Integer lastID_PROGAggr = null;
	Integer lastID_PROGVeic = null;
	
	public WebServiceImporter()
	{
		this.wsIp = Keys.WS_IP;
		this.wsPort = Keys.WS_PORT;
		this.wsUrl = Keys.WS_URL;
	}
	
	public void run()
	{
		Long start = 0L;
		
		int idInizioAggr = 0;
		int idFineAggr = 0;
		int nApparatoAggr = 0;
		
		int idInizioVeic = 0;
		int idFineVeic = 0;
		int nApparatoVeic = 0;

		String methodNameAggr = "";
		String methodNameVeic = "";
		String pathArchiveAggr = "";
		String pathArchiveVeic = "";
		
		if(Keys.WS_AGGREGATI_ENABLED)
		{
			idInizioAggr = FileUtils.getLastIdFromFile(Keys.WS_AGGREGATI_LASTID_FILE);
			methodNameAggr = Keys.WS_AGGREGATI_METHOD_NAME;
			pathArchiveAggr = Keys.WS_ARCHIVE_BASE_DIR + File.separatorChar + Keys.WS_AGGREGATI_ARCHIVE_DIR;
		}
		
		if(Keys.WS_VEICOLARI_ENABLED)
		{
			idInizioVeic = FileUtils.getLastIdFromFile(Keys.WS_VEICOLARI_LASTID_FILE);
			methodNameVeic = Keys.WS_VEICOLARI_METHOD_NAME;
			pathArchiveVeic = Keys.WS_ARCHIVE_BASE_DIR + File.separatorChar + Keys.WS_VEICOLARI_ARCHIVE_DIR;
		}
		
		log4j.info("=============================");

		while(!this.stop)
		{
			try
			{				
				if (System.currentTimeMillis() - start > (int)(Keys.WS_POLLING_TIME_MIN * 1000 * 60))
				{
					log4j.info("WebServiceImporter is starting to call the web service " + this.wsIp + ":" +this.wsPort + " ... ");
					
					if(Keys.WS_AGGREGATI_ENABLED)
					{
						if(lastID_PROGAggr != null)
						{
							idInizioAggr = lastID_PROGAggr;
						}

						ArrayList<Aggregati> listaAggregati = callWebServiceAggregati(this.wsUrl, methodNameAggr, idInizioAggr, idFineAggr, nApparatoAggr);
						
						if(Keys.WS_AGGREGATI_DB_WRITE_ENABLED)
						{
							if(listaAggregati != null && listaAggregati.size() > 0)
							{
								log4j.info("Starting write DatiAggregati in DB..");
								writeOnDbListaAggregati(listaAggregati);
							}
							else
							{
								log4j.info("No DatiAggregati to write on DB... ");
							}
						}
						 
						if(Keys.WS_AGGREGATI_ARCHIVE_ENABLED)
						{
							if(listaAggregati != null)
							{
								log4j.info("Starting write DatiAggregati in CSV..");
								writeListaAggregati(listaAggregati, pathArchiveAggr);
							}
							else
							{
								log4j.info("No DatiAggregati to write on CSV... ");
							}
						}

					}
					
					if(Keys.WS_VEICOLARI_ENABLED)
					{
						if(lastID_PROGVeic != null)
						{
							idInizioVeic = lastID_PROGVeic;
						}
						
						ArrayList<DatiVeicolo> listaDatiVeicolo = callWebServiceDatiVeicolo(this.wsUrl, methodNameVeic, idInizioVeic, idFineVeic, nApparatoVeic);
						
						if (Keys.WS_VEICOLARI_ARCHIVE_ENABLED)
						{
							if (listaDatiVeicolo != null)
							{
								log4j.info("Starting write DatiVeicolo in CSV..");
								writeListaDatiVeicolo(listaDatiVeicolo, pathArchiveVeic);
							}
							else
							{
								log4j.info("No DatiVeicolo to write in CSV... ");
							}
						}
					}
					
					log4j.info("Ok, WebServiceImporter executed. ");
					log4j.info("=============================");

					start = System.currentTimeMillis();
				}
				else
				{
					Thread.sleep(100);
				}

			}
			catch (Exception e)
			{
				log4j.error(e,e);
				start = System.currentTimeMillis();
				stopWebServiceImporter();
			}
		}
	}

	private void writeOnDbListaAggregati(ArrayList<Aggregati> listaAggregati)
	{
		DbConnection dbConn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		
		int res = 0;
		
		String sql = "insert into DatiAggregati (IdAggregato, IdSezione, DataOra, Periodicita, IdCorsia, NumeroVeicoli, VelocitaMedia, HeadwayMedio, GapMedio) values ( ?, (select IdSezione from Sezioni where TagSezione = ?), STR_TO_DATE(?,'%Y-%c-%d %H:%i:%s'), ?, ?, ?, ?, ?, ? );";
		
		try
		{			
			dbConn = new DbConnection(Keys.DB_DRIVER, Keys.DB_URL, Keys.DB_USER, Keys.DB_PWD);
			conn = dbConn.getConnection();
			
			ps = conn.prepareStatement(sql);
			
			for(Aggregati data:listaAggregati)
			{
				setVarchar(ps, 1, data.getID_PROG());
				setVarchar(ps, 2, data.getHANDLE());
				setVarchar(ps, 3, data.getTimestampFineAggr());
				setVarchar(ps, 4, data.getNTERV_AGGR());
				setVarchar(ps, 5, data.getCORSIA());
				setVarchar(ps, 6, data.getTOT_TRANSITI());
				setVarchar(ps, 7, data.getVELMED());
				setVarchar(ps, 8, data.getHEADWAY());
				setVarchar(ps, 9, data.getGAP());
			    
				log4j.trace(ps.toString().substring(ps.toString().indexOf(":") + 1 ));
				
			    ps.addBatch();
			}

			int resArr[] = ps.executeBatch();
			
			for(int i=0; i<resArr.length; i++)
			{
				if(resArr[i] == Statement.SUCCESS_NO_INFO)
				{
					res++;
				}
				else if(resArr[i] >= 0)
				{
					res++;
				}
				else if(resArr[i] == Statement.EXECUTE_FAILED)
				{}
			}
			
			log4j.info("Ok, writed on DB " + res + " aggregated data.");
			
		}
		catch(Exception e)
		{
			log4j.error(e,e);
		}
	}
	
	private static void setVarchar(PreparedStatement ps, int index, String data) throws SQLException
	{
		if (data != null)
			ps.setString(index, data);
		else
			ps.setNull(index, java.sql.Types.VARCHAR);
	}

	private ArrayList<Aggregati> callWebServiceAggregati(String urlStr, String methodName, int idInizio, int idFine, int nApparato) throws MalformedURLException, XmlRpcException, URISyntaxException
	{
		 XmlRpcClient client = new XmlRpcClient();
		 
		 XmlRpcClientConfigImpl conf = new XmlRpcClientConfigImpl();
		 
		 URL url = new URL(urlStr);
		 conf.setServerURL(url);		 
		 log4j.debug("conf.getServerURL() = " + conf.getServerURL());
		 
//		 conf.setConnectionTimeout(60000);
//		 log4j.trace("conf.getConnectionTimeout() = " + conf.getConnectionTimeout());
		 
		 client.setConfig(conf);
		 
		 ArrayList<Integer> params = new ArrayList<Integer>();
		 log4j.debug("idInizio = " + idInizio);
		 params.add(idInizio);
		 params.add(idFine);
		 params.add(nApparato);
		 
		 log4j.trace("client.execute(methodName, params) ..... ");
		 Object response = client.execute(methodName, params);
		 log4j.trace("client.execute(methodName, params) ..... DONE!!!");
		 
		 
		 Object[] aggregati = (Object[]) response;
		 log4j.trace("CAST ok..");
		 
		 ArrayList<Aggregati> listaAggregati = new ArrayList<Aggregati>();
		 Aggregati aggr = null;
		 
		 for(int i = 0; i < aggregati.length; i++)
		 {
				 aggr = new Aggregati();
				 
				 Object[] temp = (Object[]) aggregati[i];
				 log4j.trace("temp.length = " + temp.length);

				 if(log4j.isTraceEnabled())
				 {
					 log4j.trace("-----------------------------");
					 log4j.trace((String)temp[0]);
					 log4j.trace((String)temp[1]);
					 log4j.trace((String)temp[2]);
					 log4j.trace((String)temp[3]);
					 log4j.trace((String)temp[4]);
					 log4j.trace((String)temp[5]);
					 log4j.trace((String)temp[6]);
					 log4j.trace((String)temp[7]);
					 log4j.trace((String)temp[8]);
					 log4j.trace((String)temp[9]);
					 log4j.trace((String)temp[10]);
					 log4j.trace((String)temp[11]);
					 log4j.trace((String)temp[12]);
					 log4j.trace((String)temp[13]);
					 log4j.trace((String)temp[14]);
					 log4j.trace((String)temp[15]);
					 log4j.trace((String)temp[16]);
					 log4j.trace((String)temp[17]);
					 log4j.trace((String)temp[18]);
					 log4j.trace((String)temp[19]);
					 log4j.trace((String)temp[20]);
					 log4j.trace((String)temp[21]);
					 log4j.trace((String)temp[22]);
					 log4j.trace((String)temp[23]);
					 log4j.trace((String)temp[24]);
					 log4j.trace((String)temp[25]);
					 log4j.trace((String)temp[26]);
					 log4j.trace((String)temp[27]);
					 log4j.trace((String)temp[28]);
					 log4j.trace((String)temp[29]);
					 log4j.trace((String)temp[30]);
					 log4j.trace("-----------------------------");
				 }
				 
				 aggr.setID_PROG(((String)temp[0]));
				 aggr.setGiorno(((String)temp[1]));
				 aggr.setMese(((String)temp[2]));
				 aggr.setAnno(((String)temp[3]));
				 aggr.setOra(((String)temp[4]));
				 aggr.setMinuti(((String)temp[5]));
				 aggr.setSecondi(((String)temp[6]));
				 aggr.setCentesimi(((String)temp[7]));
				 aggr.setID_SITO(((String)temp[8]));
				 aggr.setID_WS(((String)temp[9]));
				 aggr.setID_AMBITO(((String)temp[10]));
				 aggr.setHANDLE(((String)temp[11]));
				 aggr.setNTERV_AGGR(((String)temp[12]));
				 aggr.setCORSIA(((String)temp[13]));
				 aggr.setVELMED(((String)temp[14]));
				 aggr.setDST_VELMED(((String)temp[15]));
				 aggr.setTOT_TRANSITI(((String)temp[16]));
				 aggr.setGAP(((String)temp[17]));
				 aggr.setDST_GAP(((String)temp[18]));
				 aggr.setHEADWAY(((String)temp[19]));
				 aggr.setDST_HEADWAY(((String)temp[20]));
				 aggr.setVELMED_XCLASSE(((String)temp[21]));
				 aggr.setDST_VELMED_XCLASSE(((String)temp[22]));
				 aggr.setTOTALI_XCLASSE(((String)temp[23]));
				 aggr.setTOTALI_XCATVEL(((String)temp[24]));
				 aggr.setDENSITA_KM(((String)temp[25]));
				 aggr.setTRAFFICO_RALL(((String)temp[26]));
				 aggr.setTRAFFICO_FERMO(((String)temp[27]));
				 aggr.setLIVELLO_OCCUP(((String)temp[28]));
				 aggr.setSTATO_PLAUSIBILITA(((String)temp[29]));
				 aggr.setDirezione(((String)temp[30]));
				 
				 listaAggregati.add(aggr);
				 this.lastID_PROGAggr = Integer.parseInt(aggr.getID_PROG())+1;
		 }
		 
		 if(listaAggregati.size() > 0)
		 {
			 log4j.info("Ok, get " + listaAggregati.size() + " aggregated data... ");
		 }
		 else
		 {
			 log4j.warn("No data found... ");
			 listaAggregati = null;
		 }
		 
		 FileUtils.flushLastIdOnFile(Keys.WS_AGGREGATI_LASTID_FILE, String.valueOf(this.lastID_PROGAggr));
		 
		 return listaAggregati;		
	}

	private ArrayList<DatiVeicolo> callWebServiceDatiVeicolo(String urlStr, String methodName, int idInizio, int idFine, int nApparato) throws MalformedURLException, URISyntaxException, XmlRpcException
	{
		XmlRpcClient client = new XmlRpcClient();
		 
		 XmlRpcClientConfigImpl conf = new XmlRpcClientConfigImpl();
		 
		 URL url = new URL(urlStr);		 
		 conf.setServerURL(url);
		 log4j.trace("conf.getServerURL() = " + conf.getServerURL());
		 
//		 conf.setConnectionTimeout(60000);
//		 log4j.trace("conf.getConnectionTimeout() = " + conf.getConnectionTimeout());
		 
		 client.setConfig(conf);

		 ArrayList<Integer> params = new ArrayList<Integer>();
		 log4j.debug("idInizio = " + idInizio);
		 params.add(idInizio);
		 params.add(idFine);
		 params.add(nApparato);
		 
		 log4j.debug("client.execute(methodName, params) ..... " + methodName );
		 Object response = client.execute(methodName, params);
		 log4j.debug("client.execute(methodName, params) ..... DONE!!!");
		 
		 
		 Object[] aggregati = (Object[]) response;
		 log4j.debug("CAST ok..");
		 
		 log4j.debug("Numero dati veicolari ricevuti = " + aggregati.length);
		 
		 ArrayList<DatiVeicolo> listaDatiVeicolo = new ArrayList<DatiVeicolo>();
		 DatiVeicolo vehicle = null;
		 
		 for(int i = 0; i < aggregati.length; i++)
		 {
			 vehicle = new DatiVeicolo();
				 
				 Object[] temp = (Object[]) aggregati[i];
				 log4j.trace("temp.length = " + temp.length);
				 
				 vehicle.setID_PROG(((String)temp[0]));
				 vehicle.setGiorno(((String)temp[1]));
				 vehicle.setMese(((String)temp[2]));
				 vehicle.setAnno(((String)temp[3]));
				 vehicle.setOra(((String)temp[4]));
				 vehicle.setMinuti(((String)temp[5]));
				 vehicle.setSecondi(((String)temp[6]));
				 vehicle.setMillisec(((String)temp[7]));
				 vehicle.setID_SITO(((String)temp[8]));
				 vehicle.setID_WS(((String)temp[9]));
				 vehicle.setID_AMBITO(((String)temp[10]));
				 vehicle.setHANDLE(((String)temp[11]));
				 vehicle.setCorsia(((String)temp[12]));
				 vehicle.setDirezione(((String)temp[13]));
				 vehicle.setLunghezza(((String)temp[14]));
				 vehicle.setClasse(((String)temp[15]));
				 vehicle.setVelocita(((String)temp[16]));
				 vehicle.setGap(((String)temp[17]));
				 vehicle.setTempo_occupazione(((String)temp[18]));
				 vehicle.setHeadway(((String)temp[19]));
				 vehicle.setPLAUSIBILTA(((String)temp[20]));
				 
				 listaDatiVeicolo.add(vehicle);
				 
				 this.lastID_PROGVeic = Integer.parseInt(vehicle.getID_PROG())+1;
		 }
		 
		 
		 if(listaDatiVeicolo.size() > 0)
		 {
			 log4j.info("Ok, get " + listaDatiVeicolo.size() + " DatiVeicolo... ");
		 }
		 else
		 {
			 log4j.warn("No data found... ");
			 listaDatiVeicolo = null;
		 }
		 
		 FileUtils.flushLastIdOnFile(Keys.WS_VEICOLARI_LASTID_FILE, String.valueOf(this.lastID_PROGVeic));
		 
		 return listaDatiVeicolo;	
	}
	
	private void writeListaAggregati(ArrayList<Aggregati> listaAggregati, String path)
	{
		String name = "FAMAS_AGGR_" + System.currentTimeMillis();
		
		File file = null;
		FileOutputStream file_os = null;
		PrintStream output = null;
		
		Aggregati temp = null;
		
		String separatorChar = ";";

		try
		{
			file = new File(path + File.separator + name + ".csv");
			if (file.exists())
			{
				file = new File(path + File.separator + name + "_" + System.currentTimeMillis() + ".csv");
			}

			file_os = new FileOutputStream(file, false);
			output = new PrintStream(file_os);
			
			String head = "ID_PROG;giorno;mese;anno;ora;minuti;secondi;centesimi;ID_SITO;ID_WS;ID_AMBITO;HANDLE;NTERV_AGGR;CORSIA;VELMED;DST_VELMED;TOT_TRANSITI;GAP;DST_GAP;HEADWAY;DST_HEADWAY;VELMED_XCLASSE;DST_VELMED_XCLASSE;TOTALI_XCLASSE;TOTALI_XCATVEL;DENSITA_KM;TRAFFICO_RALL;TRAFFICO_FERMO;LIVELLO_OCCUP;STATO_PLAUSIBILITA;Direzione;";
			output.println(head);
			
			for(int i = 0; i < listaAggregati.size(); i++)
			{
				temp = listaAggregati.get(i);
				output.print(temp.getID_PROG());
				output.print(separatorChar);
				output.print(temp.getGiorno());
				output.print(separatorChar);
				output.print(temp.getMese());
				output.print(separatorChar);
				output.print(temp.getAnno());
				output.print(separatorChar);
				output.print(temp.getOra());
				output.print(separatorChar);
				output.print(temp.getMinuti());
				output.print(separatorChar);
				output.print(temp.getSecondi());
				output.print(separatorChar);
				output.print(temp.getCentesimi());
				output.print(separatorChar);
				output.print(temp.getID_SITO());
				output.print(separatorChar);
				output.print(temp.getID_WS());
				output.print(separatorChar);
				output.print(temp.getID_AMBITO());
				output.print(separatorChar);
				output.print(temp.getHANDLE());
				output.print(separatorChar);
				output.print(temp.getNTERV_AGGR());
				output.print(separatorChar);
				output.print(temp.getCORSIA());
				output.print(separatorChar);
				output.print(temp.getVELMED());
				output.print(separatorChar);
				output.print(temp.getDST_VELMED());
				output.print(separatorChar);
				output.print(temp.getTOT_TRANSITI());
				output.print(separatorChar);
				output.print(temp.getGAP());
				output.print(separatorChar);
				output.print(temp.getDST_GAP());
				output.print(separatorChar);
				output.print(temp.getHEADWAY());
				output.print(separatorChar);
				output.print(temp.getDST_HEADWAY());
				output.print(separatorChar);
				output.print(temp.getVELMED_XCLASSE());
				output.print(separatorChar);
				output.print(temp.getDST_VELMED_XCLASSE());
				output.print(separatorChar);
				output.print(temp.getTOTALI_XCLASSE());
				output.print(separatorChar);
				output.print(temp.getTOTALI_XCATVEL());
				output.print(separatorChar);
				output.print(temp.getDENSITA_KM());
				output.print(separatorChar);
				output.print(temp.getTRAFFICO_RALL());
				output.print(separatorChar);
				output.print(temp.getTRAFFICO_FERMO());
				output.print(separatorChar);
				output.print(temp.getLIVELLO_OCCUP());
				output.print(separatorChar);
				output.print(temp.getSTATO_PLAUSIBILITA());
				output.print(separatorChar);
				output.print(temp.getDirezione());
				output.println(separatorChar);
			}
			

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
	
	private void writeListaDatiVeicolo(ArrayList<DatiVeicolo> listaDatiVeicolo, String path)
	{
		String name = "FAMAS_VEIC_" + System.currentTimeMillis();
		
		File file = null;
		FileOutputStream file_os = null;
		PrintStream output = null;
		
		DatiVeicolo temp = null;
		
		String separatorChar = ";";

		try
		{
			file = new File(path + File.separator + name + ".csv");
			if (file.exists())
			{
				file = new File(path + File.separator + name + "_" + System.currentTimeMillis() + ".csv");
			}

			file_os = new FileOutputStream(file, false);
			output = new PrintStream(file_os);
			
			String head = "ID_PROG;giorno;mese;anno;ora;minuti;secondi;millisec;ID_SITO;ID_WS;ID_AMBITO;HANDLE;corsia;direzione;lunghezza;classe;velocità;gap;tempo_occupazione;headway;PLAUSIBILTA;";
			output.println(head);
			
			for(int i = 0; i < listaDatiVeicolo.size(); i++)
			{
				temp = listaDatiVeicolo.get(i);
				output.print(temp.getID_PROG());
				output.print(separatorChar);
				output.print(temp.getGiorno());
				output.print(separatorChar);
				output.print(temp.getMese());
				output.print(separatorChar);
				output.print(temp.getAnno());
				output.print(separatorChar);
				output.print(temp.getOra());
				output.print(separatorChar);
				output.print(temp.getMinuti());
				output.print(separatorChar);
				output.print(temp.getSecondi());
				output.print(separatorChar);
				output.print(temp.getMillisec());
				output.print(separatorChar);
				output.print(temp.getID_SITO());
				output.print(separatorChar);
				output.print(temp.getID_WS());
				output.print(separatorChar);
				output.print(temp.getID_AMBITO());
				output.print(separatorChar);
				output.print(temp.getHANDLE());
				output.print(separatorChar);
				output.print(temp.getCorsia());
				output.print(separatorChar);
				output.print(temp.getDirezione());
				output.print(separatorChar);
				output.print(temp.getLunghezza());
				output.print(separatorChar);
				output.print(temp.getClasse());
				output.print(separatorChar);
				output.print(temp.getVelocita());
				output.print(separatorChar);
				output.print(temp.getGap());
				output.print(separatorChar);
				output.print(temp.getTempo_occupazione());
				output.print(separatorChar);
				output.print(temp.getHeadway());
				output.print(separatorChar);
				output.print(temp.getPLAUSIBILTA());
				output.println(separatorChar);
			}
			

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

	public void stopWebServiceImporter()
	{
		this.stop = true;
	}
}
