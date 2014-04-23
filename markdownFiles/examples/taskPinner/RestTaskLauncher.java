import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.api.TaskManager;
import sailpoint.object.Custom;
import sailpoint.object.Identity;
import sailpoint.object.TaskDefinition;
import sailpoint.rest.BaseResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * REST API to launch a task on a server.
 *
 */

@Path("restTaskLauncher")
public class RestTaskLauncher extends BaseResource {

    private static Log log=LogFactory.getLog(RestTaskLauncher.class);
    private SailPointContext _context = null;

    @POST
    @Path("LaunchServerTaskByServerNameTaskDef")
    @Consumes("application/json")
    public Response assignServerTaskByServerNameTaskDef(Map serverTaskInfo) {
        String returnString = "";
        log.debug("info passed by restcall: " + serverTaskInfo.toString());

        //get server and task information.
        String server =(String)serverTaskInfo.get("serverName");
        String task = (String)serverTaskInfo.get("taskDef");

        try {
            this._context = SailPointFactory.getCurrentContext();
            //Check the task existed.
            TaskDefinition taskDef = (TaskDefinition)this._context.getObjectById(TaskDefinition.class, task);
            if (taskDef != null) {
                String taskName = taskDef.getName();

                //check if the login user is allowed to run the task.
                Identity loggedInUser = this.getLoggedInUser();
                String userName = loggedInUser.getName();
                log.debug("Rest Api getUserName: " + userName);

                //check if the user belongs to the authorized group or is the authorized identity.
                checkForAuthorization(loggedInUser);

                //call to run the task.
                TaskManager tm = new TaskManager(this._context);
                tm.run(taskDef,null);

                returnString = "Success: TaskId-" + taskName + " successfully launched on server-" + server +".";

            }else {
                log.error("Fail: Could not find the taskId-" + task + " on server-" + server + ".");
                throw new Exception("Could not find the taskId");
                }

        }catch (Exception ex) {
            returnString = "Failed: serverName=" + server + ", taskId=" + task + ". Error: " + ex.toString();
            log.error(task + " run on " + server + " error: " + ex.toString());
        }
        return Response.status(201).entity(returnString).build();
    }

    void checkForAuthorization(Identity user) throws Exception {
        log.debug("Enter: checkForAuthorization");
        boolean isAuthorized = false;
        String customObjectName = "triumvircorp.restTaskLauncher";
        Custom custom = this._context.getObject(Custom.class, customObjectName);
        if(custom == null){
            String error = "Unable to load configuration Custom object:" + customObjectName;
            throw new Exception(error);
        }

        log.debug("Checking to see if the launcher is authorized by name");
        List<String> authorizedIdentities = (List<String>)custom.getList("authorizedIdentities");
        List<String> authorizedWorkgroups = (List<String>)custom.getList("authorizedWorkgroups");
        String userName = user.getName();
        for(String authorizedIdentity : authorizedIdentities){
            if(userName.equalsIgnoreCase(authorizedIdentity)){
                isAuthorized = true;
                log.debug("Launcher was authorized by name");
                break;
            }
        }
        if(!isAuthorized){
            log.debug("Checking authorization by workgroup");
            for(String authorizedWorkgroupName : authorizedWorkgroups){
                Identity authorizedWorkgroup = this._context.getObjectByName(Identity.class, authorizedWorkgroupName);
                if(authorizedWorkgroup != null){
                    if(user.isInWorkGroup(authorizedWorkgroup)){
                        isAuthorized = true;
                        log.debug("Launcher was authorized by workgroup:" + authorizedWorkgroupName);
                        break;
                    }
                }
            }
        }
        if (!isAuthorized) {
            String error = "The user is not authorized to run the task.\n" +
                    "Workgroups:" + authorizedWorkgroups + "\n" +
                    "Identities:" + authorizedIdentities;
            throw new Exception(error);
        }
    }

}
