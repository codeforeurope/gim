package sistematica.gim.sisasdiss.servlet;

import java.util.List;

public class SisasDissFile
{
	//id del dissusasore presente nel DB
	public String idDissuasore;
	
	//TAG del dissusasore presente nel DB corrispondente a quello dell'intestazione del file
	public String tagDissuasore;
	
	//Timestamp corrispondente a quello dell'intestazione del file 
	public String timestamp;
	
	//Periodicità di invio dei file, da file di properties
	public String periodicita;
	
	//Lista dei singoli passaggi
	public List<SisasDissData> data;
	
	//Nome del file = timestamp_tagDissuasore.txt
	public String name;
	
	//Lista delle velocità dei passaggi contenuti nel file, per calcolare stats sulle velociatà
	public List<Double> speedList;
	
	//Numero dei veicoli conteggiati nel file
	public Integer numeroVeicoli;
	
	//La velocità media calcolata
	public Double velocitaMedia;
	
	//La velocità massima calcolata
	public Double velocitaMassima;
	
	//La velocità minima calcolata
	public Double velocitaMinima;
	
}
