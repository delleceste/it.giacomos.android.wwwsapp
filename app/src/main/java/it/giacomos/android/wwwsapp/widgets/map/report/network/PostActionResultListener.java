package it.giacomos.android.wwwsapp.widgets.map.report.network;

/** After a http post has been performed, the methods of this interface are called.
 * 
 * @author giacomo
 *
 * HelloWorldActivity implements this interface.
 */
public interface PostActionResultListener 
{
	void onPostActionResult(boolean error, String message, PostType postType);

}
