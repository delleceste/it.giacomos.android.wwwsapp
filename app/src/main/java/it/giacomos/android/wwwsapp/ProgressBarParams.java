package it.giacomos.android.wwwsapp;

public class ProgressBarParams {
	
	/* 1. get situation
	 * 2. get today bmp
	 * 3. get today forecast (text only)
	 * 4. get tomorrow forecast (text  + bmp)
	 * 5. get two days forecast (text  + bmp)
	 */
	public static final double TOTAL_TASKS_INIT = 7.0; 
	
	/* android.app Activity progress bar goes from 0 to 10000.
	 * At 10000 the progress bar will be completely filled and will fade out.
	 */
	public static final double MAX_PB_VALUE = 10000.0;
	
	/* single task from 0 to 100% */
	public static  final double SINGLE_TASK_RANGE = 100.0;
	
	public static double currentValue;

}
