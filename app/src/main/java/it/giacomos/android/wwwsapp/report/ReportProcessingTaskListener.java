package it.giacomos.android.wwwsapp.report;

import java.util.ArrayList;
import java.util.HashMap;

public interface ReportProcessingTaskListener
{
	void onReportProcessingTaskFinished(HashMap<String , DataInterface> dataI);

}
