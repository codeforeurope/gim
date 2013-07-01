package sistematica.famasimporter.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 
 */

/**
 * @author gsilvestri
 *
 */
public class Aggregati
{
	private Logger log4j = Logger.getLogger(Aggregati.class);
	
	String ID_PROG;
	String giorno;
	String mese;
	String anno;
	String ora;
	String minuti;
	String secondi;
	String centesimi;
	String ID_SITO;
	String ID_WS;
	String ID_AMBITO;
	String HANDLE;
	String NTERV_AGGR;
	String CORSIA;
	String VELMED;
	String DST_VELMED;
	String TOT_TRANSITI;
	String GAP;
	String DST_GAP;
	String HEADWAY;
	String DST_HEADWAY;
	String VELMED_XCLASSE;
	String DST_VELMED_XCLASSE;
	String TOTALI_XCLASSE;
	String TOTALI_XCATVEL;
	String DENSITA_KM;
	String TRAFFICO_RALL;
	String TRAFFICO_FERMO;
	String LIVELLO_OCCUP;
	String STATO_PLAUSIBILITA;
	String direzione;

	public Aggregati()
	{}

	public Aggregati(String iD_PROG, String giorno, String mese, String anno, String ora, String minuti, String secondi, String centesimi, String iD_SITO, String iD_WS, String iD_AMBITO, String hANDLE,
			String nTERV_AGGR, String cORSIA, String vELMED, String dST_VELMED, String tOT_TRANSITI, String gAP, String dST_GAP, String hEADWAY, String dST_HEADWAY, String vELMED_XCLASSE,
			String dST_VELMED_XCLASSE, String tOTALI_XCLASSE, String tOTALI_XCATVEL, String dENSITA_KM, String tRAFFICO_RALL, String tRAFFICO_FERMO, String lIVELLO_OCCUP, String sTATO_PLAUSIBILITA, String direzione)
	{
		super();
		ID_PROG = iD_PROG;
		this.giorno = giorno;
		this.mese = mese;
		this.anno = anno;
		this.ora = ora;
		this.minuti = minuti;
		this.secondi = secondi;
		this.centesimi = centesimi;
		ID_SITO = iD_SITO;
		ID_WS = iD_WS;
		ID_AMBITO = iD_AMBITO;
		HANDLE = hANDLE;
		NTERV_AGGR = nTERV_AGGR;
		CORSIA = cORSIA;
		VELMED = vELMED;
		DST_VELMED = dST_VELMED;
		TOT_TRANSITI = tOT_TRANSITI;
		GAP = gAP;
		DST_GAP = dST_GAP;
		HEADWAY = hEADWAY;
		DST_HEADWAY = dST_HEADWAY;
		VELMED_XCLASSE = vELMED_XCLASSE;
		DST_VELMED_XCLASSE = dST_VELMED_XCLASSE;
		TOTALI_XCLASSE = tOTALI_XCLASSE;
		TOTALI_XCATVEL = tOTALI_XCATVEL;
		DENSITA_KM = dENSITA_KM;
		TRAFFICO_RALL = tRAFFICO_RALL;
		TRAFFICO_FERMO = tRAFFICO_FERMO;
		LIVELLO_OCCUP = lIVELLO_OCCUP;
		STATO_PLAUSIBILITA = sTATO_PLAUSIBILITA;
		this.direzione = direzione;
	}

	/**
	 * @return the iD_PROG
	 */
	public String getID_PROG()
	{
		return ID_PROG;
	}

	/**
	 * @param iD_PROG the iD_PROG to set
	 */
	public void setID_PROG(String iD_PROG)
	{
		ID_PROG = iD_PROG;
	}

	/**
	 * @return the giorno
	 */
	public String getGiorno()
	{
		return giorno;
	}

	/**
	 * @param giorno the giorno to set
	 */
	public void setGiorno(String giorno)
	{
		this.giorno = giorno;
	}

	/**
	 * @return the mese
	 */
	public String getMese()
	{
		return mese;
	}

	/**
	 * @param mese the mese to set
	 */
	public void setMese(String mese)
	{
		this.mese = mese;
	}

	/**
	 * @return the anno
	 */
	public String getAnno()
	{
		return anno;
	}

	/**
	 * @param anno the anno to set
	 */
	public void setAnno(String anno)
	{
		this.anno = anno;
	}

	/**
	 * @return the ora
	 */
	public String getOra()
	{
		return ora;
	}

	/**
	 * @param ora the ora to set
	 */
	public void setOra(String ora)
	{
		this.ora = ora;
	}

	/**
	 * @return the minuti
	 */
	public String getMinuti()
	{
		return minuti;
	}

	/**
	 * @param minuti the minuti to set
	 */
	public void setMinuti(String minuti)
	{
		this.minuti = minuti;
	}

	/**
	 * @return the secondi
	 */
	public String getSecondi()
	{
		return secondi;
	}

	/**
	 * @param secondi the secondi to set
	 */
	public void setSecondi(String secondi)
	{
		this.secondi = secondi;
	}

	/**
	 * @return the centesimi
	 */
	public String getCentesimi()
	{
		return centesimi;
	}

	/**
	 * @param centesimi the centesimi to set
	 */
	public void setCentesimi(String centesimi)
	{
		this.centesimi = centesimi;
	}

	/**
	 * @return the iD_SITO
	 */
	public String getID_SITO()
	{
		return ID_SITO;
	}

	/**
	 * @param iD_SITO the iD_SITO to set
	 */
	public void setID_SITO(String iD_SITO)
	{
		ID_SITO = iD_SITO;
	}

	/**
	 * @return the iD_WS
	 */
	public String getID_WS()
	{
		return ID_WS;
	}

	/**
	 * @param iD_WS the iD_WS to set
	 */
	public void setID_WS(String iD_WS)
	{
		ID_WS = iD_WS;
	}

	/**
	 * @return the iD_AMBITO
	 */
	public String getID_AMBITO()
	{
		return ID_AMBITO;
	}

	/**
	 * @param iD_AMBITO the iD_AMBITO to set
	 */
	public void setID_AMBITO(String iD_AMBITO)
	{
		ID_AMBITO = iD_AMBITO;
	}

	/**
	 * @return the hANDLE
	 */
	public String getHANDLE()
	{
		return HANDLE;
	}

	/**
	 * @param hANDLE the hANDLE to set
	 */
	public void setHANDLE(String hANDLE)
	{
		HANDLE = hANDLE;
	}

	/**
	 * @return the nTERV_AGGR
	 */
	public String getNTERV_AGGR()
	{
		return NTERV_AGGR;
	}

	/**
	 * @param nTERV_AGGR the nTERV_AGGR to set
	 */
	public void setNTERV_AGGR(String nTERV_AGGR)
	{
		NTERV_AGGR = nTERV_AGGR;
	}

	/**
	 * @return the cORSIA
	 */
	public String getCORSIA()
	{
		return CORSIA;
	}

	/**
	 * @param cORSIA the cORSIA to set
	 */
	public void setCORSIA(String cORSIA)
	{
		CORSIA = cORSIA;
	}

	/**
	 * @return the vELMED
	 */
	public String getVELMED()
	{
		return VELMED;
	}

	/**
	 * @param vELMED the vELMED to set
	 */
	public void setVELMED(String vELMED)
	{
		VELMED = vELMED;
	}

	/**
	 * @return the dST_VELMED
	 */
	public String getDST_VELMED()
	{
		return DST_VELMED;
	}

	/**
	 * @param dST_VELMED the dST_VELMED to set
	 */
	public void setDST_VELMED(String dST_VELMED)
	{
		DST_VELMED = dST_VELMED;
	}

	/**
	 * @return the tOT_TRANSITI
	 */
	public String getTOT_TRANSITI()
	{
		return TOT_TRANSITI;
	}

	/**
	 * @param tOT_TRANSITI the tOT_TRANSITI to set
	 */
	public void setTOT_TRANSITI(String tOT_TRANSITI)
	{
		TOT_TRANSITI = tOT_TRANSITI;
	}

	/**
	 * @return the gAP
	 */
	public String getGAP()
	{
		return GAP;
	}

	/**
	 * @param gAP the gAP to set
	 */
	public void setGAP(String gAP)
	{
		GAP = gAP;
	}

	/**
	 * @return the dST_GAP
	 */
	public String getDST_GAP()
	{
		return DST_GAP;
	}

	/**
	 * @param dST_GAP the dST_GAP to set
	 */
	public void setDST_GAP(String dST_GAP)
	{
		DST_GAP = dST_GAP;
	}

	/**
	 * @return the hEADWAY
	 */
	public String getHEADWAY()
	{
		return HEADWAY;
	}

	/**
	 * @param hEADWAY the hEADWAY to set
	 */
	public void setHEADWAY(String hEADWAY)
	{
		HEADWAY = hEADWAY;
	}

	/**
	 * @return the dST_HEADWAY
	 */
	public String getDST_HEADWAY()
	{
		return DST_HEADWAY;
	}

	/**
	 * @param dST_HEADWAY the dST_HEADWAY to set
	 */
	public void setDST_HEADWAY(String dST_HEADWAY)
	{
		DST_HEADWAY = dST_HEADWAY;
	}

	/**
	 * @return the vELMED_XCLASSE
	 */
	public String getVELMED_XCLASSE()
	{
		return VELMED_XCLASSE;
	}

	/**
	 * @param vELMED_XCLASSE the vELMED_XCLASSE to set
	 */
	public void setVELMED_XCLASSE(String vELMED_XCLASSE)
	{
		VELMED_XCLASSE = vELMED_XCLASSE;
	}

	/**
	 * @return the dST_VELMED_XCLASSE
	 */
	public String getDST_VELMED_XCLASSE()
	{
		return DST_VELMED_XCLASSE;
	}

	/**
	 * @param dST_VELMED_XCLASSE the dST_VELMED_XCLASSE to set
	 */
	public void setDST_VELMED_XCLASSE(String dST_VELMED_XCLASSE)
	{
		DST_VELMED_XCLASSE = dST_VELMED_XCLASSE;
	}

	/**
	 * @return the tOTALI_XCLASSE
	 */
	public String getTOTALI_XCLASSE()
	{
		return TOTALI_XCLASSE;
	}

	/**
	 * @param tOTALI_XCLASSE the tOTALI_XCLASSE to set
	 */
	public void setTOTALI_XCLASSE(String tOTALI_XCLASSE)
	{
		TOTALI_XCLASSE = tOTALI_XCLASSE;
	}

	/**
	 * @return the tOTALI_XCATVEL
	 */
	public String getTOTALI_XCATVEL()
	{
		return TOTALI_XCATVEL;
	}

	/**
	 * @param tOTALI_XCATVEL the tOTALI_XCATVEL to set
	 */
	public void setTOTALI_XCATVEL(String tOTALI_XCATVEL)
	{
		TOTALI_XCATVEL = tOTALI_XCATVEL;
	}

	/**
	 * @return the dENSITA_KM
	 */
	public String getDENSITA_KM()
	{
		return DENSITA_KM;
	}

	/**
	 * @param dENSITA_KM the dENSITA_KM to set
	 */
	public void setDENSITA_KM(String dENSITA_KM)
	{
		DENSITA_KM = dENSITA_KM;
	}

	/**
	 * @return the tRAFFICO_RALL
	 */
	public String getTRAFFICO_RALL()
	{
		return TRAFFICO_RALL;
	}

	/**
	 * @param tRAFFICO_RALL the tRAFFICO_RALL to set
	 */
	public void setTRAFFICO_RALL(String tRAFFICO_RALL)
	{
		TRAFFICO_RALL = tRAFFICO_RALL;
	}

	/**
	 * @return the tRAFFICO_FERMO
	 */
	public String getTRAFFICO_FERMO()
	{
		return TRAFFICO_FERMO;
	}

	/**
	 * @param tRAFFICO_FERMO the tRAFFICO_FERMO to set
	 */
	public void setTRAFFICO_FERMO(String tRAFFICO_FERMO)
	{
		TRAFFICO_FERMO = tRAFFICO_FERMO;
	}

	/**
	 * @return the lIVELLO_OCCUP
	 */
	public String getLIVELLO_OCCUP()
	{
		return LIVELLO_OCCUP;
	}

	/**
	 * @param lIVELLO_OCCUP the lIVELLO_OCCUP to set
	 */
	public void setLIVELLO_OCCUP(String lIVELLO_OCCUP)
	{
		LIVELLO_OCCUP = lIVELLO_OCCUP;
	}

	/**
	 * @return the sTATO_PLAUSIBILITA
	 */
	public String getSTATO_PLAUSIBILITA()
	{
		return STATO_PLAUSIBILITA;
	}

	/**
	 * @param sTATO_PLAUSIBILITA the sTATO_PLAUSIBILITA to set
	 */
	public void setSTATO_PLAUSIBILITA(String sTATO_PLAUSIBILITA)
	{
		STATO_PLAUSIBILITA = sTATO_PLAUSIBILITA;
	}

	/**
	 * @return the end
	 */
	public String getDirezione()
	{
		return direzione;
	}

	/**
	 * @param end the end to set
	 */
	public void setDirezione(String direzione)
	{
		this.direzione = direzione;
	}
	
	public String getTimestampInizioAggr()
	{
		return this.anno + "-" + this.mese + "-" + this.giorno + " " + this.ora + ":" + this.minuti + ":" + this.secondi;
	}
	
	public String getTimestampFineAggr()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		Long dateMilli = null; 

		try
		{
			//prendo la data di inzio aggregazione
			date = sdf.parse(getTimestampInizioAggr());
			dateMilli = date.getTime();
			
			//gli aggiungo l'intervallo di aggregazione
			dateMilli = dateMilli + (long)(Integer.parseInt(this.NTERV_AGGR) * 60 * 1000);
			
			date = new Date(dateMilli);
		}
		catch(Exception e)
		{
			log4j.error(e,e);
			return getTimestampInizioAggr();
		}
		
		return sdf.format(date);
	}
}
