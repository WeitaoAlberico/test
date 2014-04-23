import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import sailpoint.api.SailPointContext;
import sailpoint.object.*;
import sailpoint.task.AbstractTaskExecutor;
import sailpoint.task.Monitor;
import sailpoint.task.TaskMonitor;
import sailpoint.tools.Message;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class RestTaskLauncherExecutor extends AbstractTaskExecutor {
    private static Log log = LogFactory.getLog(RestTaskLauncherExecutor.class);

    private SailPointContext _context = null;
    private TaskResult _taskResult = null;
    private TaskSchedule _taskSchedule = null;
    private Attributes<String, Object> _arguments = null;
    private Monitor _monitor = null;

    private boolean _shouldTerminate = false;

    private String urlEndPoint = "/rest/restTaskLauncher/LaunchServerTaskByServerNameTaskDef";
    private String urlStartPoint = "";


    /**
     * get the authentication user account and its password, then launch the remote task call.
     * @param sailPointContext
     * @param taskSchedule
     * @param attributes
     */
    public void execute(SailPointContext sailPointContext, TaskSchedule taskSchedule, TaskResult taskResult, Attributes<String, Object> attributes) throws Exception {
        this._context = sailPointContext;
        this._taskSchedule = taskSchedule;
        this._taskResult = taskResult;
        this._arguments = attributes;
        this._monitor = new TaskMonitor(_context, taskResult);

        log.debug("start task launcher executor......");
        log.debug("TaskDefAttributes: " + _arguments.toString());


        String user = (String) this._arguments.get("user");
        if (user != null && !user.isEmpty()) {
            log.debug("user is: " + user);

            //get user and password.
            String userpwdForConn = "";
            Identity authIdentity = _context.getObjectByName(Identity.class, user);
            if (authIdentity != null) {
                String userName = authIdentity.getName();
                String password = _context.decrypt(authIdentity.getPassword());
                userpwdForConn = userName + ":" + password;
                log.debug("userpassword string for connection is: " + userpwdForConn);
                launchRestTaskCalls(userpwdForConn);
            } else {
                _taskResult.addMessage(Message.error("The User could not be found."));
            }
        } else {
            log.error("user name is empty.");
            _taskResult.addMessage(Message.error("User name is required."));
        }


    }

    /*
     * loop through all 10 servers inputs, call server and its task if their values are not null.
     * @param userpwdForConn
     * @return
     */
    void launchRestTaskCalls(String userpwdForConn) {
        Set<String> serverTaskHashSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        int numberofsuccess = 0;
        int numberoffail = 0;

        log.debug("start loop through servers.");
        //loop 10 server-task pairs values

        for (int i = 1; i <=10; i++) {
            log.debug("start loop " + i);
            String serverName = "";
            String taskId = "";
            try {
                serverName =(String) this._arguments.get("server" + i);
                taskId =(String) this._arguments.get("task" + i);
                if (serverName == null) {log.debug("server" + i + " is null.");serverName = "";}
                if (taskId == null) {log.debug("task" + i + " is null."); taskId = "";}
            } catch (Exception ex) {
                log.debug("error getting server" + i + " and task" + i + " names.");
            }

            if (!serverName.isEmpty() && !taskId.isEmpty()) {
                //get rid of the last "/" if there is one.
                if (serverName.lastIndexOf("/")==(serverName.length()-1)) {
                    serverName = serverName.substring(0, serverName.length()-1);
                }
                log.debug("server" + i + ": " + serverName);
                log.debug("task" + i + ": " + taskId);

                //call launch function.
                if (!serverTaskHashSet.contains(serverName + taskId)) {
                    serverTaskHashSet.add(serverName + taskId);
                    try {
                        String taskName = this._context.getObjectById(TaskDefinition.class, taskId).getName();
                        restTaskCall(serverName, taskId, taskName, userpwdForConn);
                        this._taskResult.addMessage(Message.info("Successfully launched: serverName=" + serverName + ", taskName=" + taskName));
                        numberofsuccess++;
                    } catch (Exception ex) {
                        this._taskResult.addMessage(Message.error(ex.toString()));
                        numberoffail++;
                        log.error("rest Task (" + serverName + ", " + taskId + ") Call failed-" + ex.toString());
                    }
                }
            }
        }

        this._taskResult.setAttribute("numberOfSuccessfullyLaunched", numberofsuccess);
        this._taskResult.setAttribute("numberOfFailed", numberoffail);
        log.debug("after loop servers function. The servertask Hashset is: " + serverTaskHashSet.toString());

    }

    /*
     * set up the connection to the remote server, then call the rest api.
     * @param serverName
     * @param taskId
     * @param taskName
     * @param userpwdForConn
     * @return
     */
    void restTaskCall(String serverName, String taskId, String taskName, String userpwdForConn) {
        try {
            Map<String, String> serverTaskPair = new HashMap<String, String>();
            serverTaskPair.put("serverName", serverName);
            serverTaskPair.put("taskDef", taskId);

            //set up the url connection.
            String urlString = this.urlStartPoint + serverName + this.urlEndPoint;
            log.debug("URL: " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            //set the url connection authentication.
            String usrpwdforConn = userpwdForConn;
            String usrpwdEncoded = new BASE64Encoder().encode(usrpwdforConn.getBytes());

            conn.setRequestProperty("Authorization", "Basic " + usrpwdEncoded);


            conn.setRequestProperty("Content-Type", "application/json");
            //generate the connection content data.
            JSONObject json = new JSONObject(serverTaskPair);
            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode() + ", serverName=" + serverName + ", taskName=" + taskName);
            }

            //log the server response. If the response message is "fail", then throw exception.
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String output = "Launch was successful with connection response: ";
            log.debug(output);
            while ((output = br.readLine()) != null) {
                log.debug(output);
                if (output.contains("Fail")) {
                    throw new RuntimeException(output);
                }
            }
            log.debug("#endofresponse.");

        } catch (MalformedURLException e) {
            log.error("Failed: MalformedURLException, serverName=" + serverName + ", taskName=" + taskName);
            throw new RuntimeException("Failed: MalformedURLException, serverName=" + serverName + ", taskName=" + taskName);
        } catch (IOException e) {
            log.error("Failed: IOException, serverName=" + serverName + ", taskName=" + taskName);
            e.printStackTrace();
            throw new RuntimeException("Failed: IOException, serverName=" + serverName + ", taskName=" + taskName);
        }
    }

    @Override
    public boolean terminate() {
        this._shouldTerminate = true;
        return this._shouldTerminate;
    }

}

