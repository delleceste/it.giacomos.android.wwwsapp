package it.giacomos.android.wwwsapp.regexps;

public class Regexps {
	public static final String SITUATION = "SITUAZIONEGENERALE_TESTO=(.*)";
	/* Oggi.info, Domani.info, Dopodomani.info */
	public static final String DATE = "DATA DI RIFERIMENTO:\\s*(.*?)\\s*DATA";
	public static final String RELIABILITY = "ATTENDIBILITA':\\s*\\n\\s*([0-9]{1,3})\\s*";
	public static final String EMISSION_DATE = "DATA DI EMISSIONE:\\s*(.*?)\\n";
	public static final String EMISSION_HOUR = "ORA DI EMISSIONE:\\s*(.*?)\\n";
	public static final String INFO_TXT = "TESTO:\\n(.*?)\\n";

	/* webcam extraction regexps from DatiWebcams1.php */
	
	/* if\s+\(nome\s+==\s+\"([a-zA-Z0-9_\-\.\s]+)\"\) */
	public static final String WEBCAM_LOCATION = "if\\s+\\(nome\\s+==\\s+\\\"([a-zA-Z0-9_\\-\\.\\s]+)\\\"\\)";
	
	/* \s*var\s+nome1\s*\=\"([A-Za-z0-9_\-\.\s]+)\"\s* 
	 *
	 */
	public static final String WEBCAM_FILENAME = "\\s*var\\s+nome1\\s*\\=\\\"([A-Za-z0-9_\\-\\.\\s]+)\\\"\\s*";
	
	/* 
	 *    \s*var\s+str\s*\=\"([A-Za-z0-9_\-\.\s/:]+)\"\s*
	 */
	public static final String WEBCAM_TEXT = "\\s*var\\s+str\\s*\\=\\\"([A-Za-z0-9_\\-\\.\\s/:]+)\\\"\\s*";
	
}
