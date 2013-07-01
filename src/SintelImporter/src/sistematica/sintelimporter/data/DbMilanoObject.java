/**
 * 
 */
package sistematica.sintelimporter.data;

/**
 * @author gsilvestri
 *
 */
public class DbMilanoObject
{
	String tagMoviTraff;
	String idAggregato;
	String dataOra;
	String periodicita;
	String idCorsia;
	String numeroVeicoli;
	String velocitaMedia;
	String lunghezzaMedia;
	String headWayMedio;
	String gapMedio;
	String occupazione;
	String diagnostica;
	
	public DbMilanoObject(String tagMoviTraff, String idAggregato, String dataOra, String periodicita, String idCorsia, String numeroVeicoli, String velocitaMedia,
			String lunghezzaMedia, String headWayMedio, String gapMedio, String occupazione, String diagnostica)
	{
		super();
		this.tagMoviTraff = tagMoviTraff;
		this.idAggregato = idAggregato;
		this.dataOra = dataOra;
		this.periodicita = periodicita;
		this.idCorsia = idCorsia;
		this.numeroVeicoli = numeroVeicoli;
		this.velocitaMedia = velocitaMedia;
		this.lunghezzaMedia = lunghezzaMedia;
		this.headWayMedio = headWayMedio;
		this.gapMedio = gapMedio;
		this.occupazione = occupazione;
		this.diagnostica = diagnostica;
	}

	/**
	 * @return the tagMoviTraff
	 */
	public String getTagMoviTraff()
	{
		return tagMoviTraff;
	}

	/**
	 * @param tagMoviTraff the tagMoviTraff to set
	 */
	public void setTagMoviTraff(String tagMoviTraff)
	{
		this.tagMoviTraff = tagMoviTraff;
	}

	/**
	 * @return the idAggregato
	 */
	public String getIdAggregato()
	{
		return idAggregato;
	}

	/**
	 * @param idAggregato the idAggregato to set
	 */
	public void setIdAggregato(String idAggregato)
	{
		this.idAggregato = idAggregato;
	}

	/**
	 * @return the dataOra
	 */
	public String getDataOra()
	{
		return dataOra;
	}

	/**
	 * @param dataOra the dataOra to set
	 */
	public void setDataOra(String dataOra)
	{
		this.dataOra = dataOra;
	}

	/**
	 * @return the periodicita
	 */
	public String getPeriodicita()
	{
		return periodicita;
	}

	/**
	 * @param periodicita the periodicita to set
	 */
	public void setPeriodicita(String periodicita)
	{
		this.periodicita = periodicita;
	}

	/**
	 * @return the idCorsia
	 */
	public String getIdCorsia()
	{
		return idCorsia;
	}

	/**
	 * @param idCorsia the idCorsia to set
	 */
	public void setIdCorsia(String idCorsia)
	{
		this.idCorsia = idCorsia;
	}

	/**
	 * @return the numeroVeicoli
	 */
	public String getNumeroVeicoli()
	{
		return numeroVeicoli;
	}

	/**
	 * @param numeroVeicoli the numeroVeicoli to set
	 */
	public void setNumeroVeicoli(String numeroVeicoli)
	{
		this.numeroVeicoli = numeroVeicoli;
	}

	/**
	 * @return the velocitaMedia
	 */
	public String getVelocitaMedia()
	{
		return velocitaMedia;
	}

	/**
	 * @param velocitaMedia the velocitaMedia to set
	 */
	public void setVelocitaMedia(String velocitaMedia)
	{
		this.velocitaMedia = velocitaMedia;
	}

	/**
	 * @return the lunghezzaMedia
	 */
	public String getLunghezzaMedia()
	{
		return lunghezzaMedia;
	}

	/**
	 * @param lunghezzaMedia the lunghezzaMedia to set
	 */
	public void setLunghezzaMedia(String lunghezzaMedia)
	{
		this.lunghezzaMedia = lunghezzaMedia;
	}

	/**
	 * @return the headWayMedio
	 */
	public String getHeadWayMedio()
	{
		return headWayMedio;
	}

	/**
	 * @param headWayMedio the headWayMedio to set
	 */
	public void setHeadWayMedio(String headWayMedio)
	{
		this.headWayMedio = headWayMedio;
	}

	/**
	 * @return the gapMedio
	 */
	public String getGapMedio()
	{
		return gapMedio;
	}

	/**
	 * @param gapMedio the gapMedio to set
	 */
	public void setGapMedio(String gapMedio)
	{
		this.gapMedio = gapMedio;
	}

	/**
	 * @return the occupazione
	 */
	public String getOccupazione()
	{
		return occupazione;
	}

	/**
	 * @param occupazione the occupazione to set
	 */
	public void setOccupazione(String occupazione)
	{
		this.occupazione = occupazione;
	}

	/**
	 * @return the diagnostica
	 */
	public String getDiagnostica()
	{
		return diagnostica;
	}

	/**
	 * @param diagnostica the diagnostica to set
	 */
	public void setDiagnostica(String diagnostica)
	{
		this.diagnostica = diagnostica;
	}
	
	
	
}
