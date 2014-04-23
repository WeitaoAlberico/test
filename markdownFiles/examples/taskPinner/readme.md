# Task Pinner
### __Description__

  This project uses a scheduled task to call a rest API. Then this rest API will verify the permission for the rest caller, then run the local task as requested by the rest caller.

### __Files' Detail__

 1. **Task Definition** - _TaskDef_RestCaller.xml_
    
    This task definition is a scheduled task which specifies all the parameters to authenticate, and the information of the remote tasks and remote servers.

 2. **Executor of the Task Definition** - _RestTaskLauncherExecutor.java_
    
    This Java file is the executor for the scheduled task mentioned above. It calls the rest API to launch remote tasks.

 3. **Rest API** - _RestTaskLauncher.java_
    
    This Java file defines the rest API functions which includes verifing the permissions to run the local task, then invoking the local task.

 4. **Extend the SailPointRestApplication** - _RestApplication.java_
    
    This file adds the rest API Java file mentioned above to the SailPointRestApplication getclasses call.

 5. **Custom Object** - _customObject.xml

 	This custom object file defines the authorized identities and the authorized groups. 