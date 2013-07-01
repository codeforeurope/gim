/**
 * 
 */
package sistematica.famasimporter.data;

/**
 * @author gsilvestri
 *
 */
public class DatiVeicolo
{
	String ID_PROG;
	String giorno;
	String mese;
	String anno;
	String ora;
	String minuti;
	String secondi;
	String millisec;
	String ID_SITO;
	String ID_WS;
	String ID_AMBITO;
	String HANDLE;
	String corsia;
	String direzione;
	String lunghezza;
	String classe;
	String velocita;
	String gap;
	String tempo_occupazione;
	String headway;
	String PLAUSIBILTA;
	
	public DatiVeicolo()
	{}

	public DatiVeicolo(String iD_PROG, String giorno, String mese, String anno, String ora, String minuti, String secondi, String millisec, String iD_SITO, String iD_WS,
			String iD_AMBITO, String hANDLE, String corsia, String direzione, String lunghezza, String classe, String velocita, String gap, String tempo_occupazione,
			String headway, String pLAUSIBILTA)
	{
		super();
		ID_PROG = iD_PROG;
		this.giorno = giorno;
		this.mese = mese;
		this.anno = anno;
		this.ora = ora;
		this.minuti = minuti;
		this.secondi = secondi;
		this.millisec = millisec;
		ID_SITO = iD_SITO;
		ID_WS = iD_WS;
		ID_AMBITO = iD_AMBITO;
		HANDLE = hANDLE;
		this.corsia = corsia;
		this.direzione = direzione;
		this.lunghezza = lunghezza;
		this.classe = classe;
		this.velocita = velocita;
		this.gap = gap;
		this.tempo_occupazione = tempo_occupazione;
		this.headway = headway;
		PLAUSIBILTA = pLAUSIBILTA;
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
	 * @return the millisec
	 */
	public String getMillisec()
	{
		return millisec;
	}

	/**
	 * @param millisec the millisec to set
	 */
	public void setMillisec(String millisec)
	{
		this.millisec = millisec;
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
	 * @return the corsia
	 */
	public String getCorsia()
	{
		return corsia;
	}

	/**
	 * @param corsia the corsia to set
	 */
	public void setCorsia(String corsia)
	{
		this.corsia = corsia;
	}

	/**
	 * @return the direzione
	 */
	public String getDirezione()
	{
		return direzione;
	}

	/**
	 * @param direzione the direzione to set
	 */
	public void setDirezione(String direzione)
	{
		this.direzione = direzione;
	}

	/**
	 * @return the lunghezza
	 */
	public String getLunghezza()
	{
		return lunghezza;
	}

	/**
	 * @param lunghezza the lunghezza to set
	 */
	public void setLunghezza(String lunghezza)
	{
		this.lunghezza = lunghezza;
	}

	/**
	 * @return the classe
	 */
	public String getClasse()
	{
		return classe;
	}

	/**
	 * @param classe the classe to set
	 */
	public void setClasse(String classe)
	{
		this.classe = classe;
	}

	/**
	 * @return the velocità
	 */
	public String getVelocita()
	{
		return velocita;
	}

	/**
	 * @param velocità the velocità to set
	 */
	public void setVelocita(String velocita)
	{
		this.velocita = velocita;
	}

	/**
	 * @return the gap
	 */
	public String getGap()
	{
		return gap;
	}

	/**
	 * @param gap the gap to set
	 */
	public void setGap(String gap)
	{
		this.gap = gap;
	}

	/**
	 * @return the tempo_occupazione
	 */
	public String getTempo_occupazione()
	{
		return tempo_occupazione;
	}

	/**
	 * @param tempo_occupazione the tempo_occupazione to set
	 */
	public void setTempo_occupazione(String tempo_occupazione)
	{
		this.tempo_occupazione = tempo_occupazione;
	}

	/**
	 * @return the headway
	 */
	public String getHeadway()
	{
		return headway;
	}

	/**
	 * @param headway the headway to set
	 */
	public void setHeadway(String headway)
	{
		this.headway = headway;
	}

	/**
	 * @return the pLAUSIBILTA
	 */
	public String getPLAUSIBILTA()
	{
		return PLAUSIBILTA;
	}

	/**
	 * @param pLAUSIBILTA the pLAUSIBILTA to set
	 */
	public void setPLAUSIBILTA(String pLAUSIBILTA)
	{
		PLAUSIBILTA = pLAUSIBILTA;
	}	
}
