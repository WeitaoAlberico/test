#IIQ
### get applications for a role
**input parameters:** *Bundle* bundle
**return paramters:** *Hash Set* applications
```
Set getApplicationsForRole(Bundle bundle){
	Set applications = new TreeSet(String.CASE_INSENSITIVE_ORDER);
	try{
        Set visitedBundles = new HashSet();
        
        Stack bundles = new Stack();
        bundles.push(bundle);
        visitedBundles.add(bundle.getName());
        while(!bundles.isEmpty()){
            Bundle bundle1 = bundles.pop();
            if(visitedBundles.add(bundle1)) {
                for (Profile profile : bundle1.getProfiles()) {
                    Application application = profile.getApplication();
                    if (application != null) {
                        applications.add(application.getName());
                    }
                }
                for (Bundle required : bundle1.getRequirements()) {
                    bundles.push(required);
                }
            }
        }
	}catch (Exception ex){}
	return applications;
}
```
***

#Utils
### get a file's contents
**input parameters:** *String* filename
**return parameters:** *String* fileContents
```
String getFileContents(String filename) {
		StringBuilder builder = new StringBuilder();
		try {
			if (filename == null || filename.isEmpty()) {
				throw new Exception("Invalid filename");
			}
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream dstream = new DataInputStream(fstream);
			BufferedReader bReader = new BufferedReader(new InputStreamReader(dstream));
			String line = null;
			boolean firstLine = true;
			while ((line = bReader.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
				} else {
					builder.append("\n");
				}
				builder.append(line);
			}
			dstream.close();
		} catch (Exception e) {
			System.out.println("getFileContents: " + e.getMessage());
		}
		String fileContents = builder.toString();
		return fileContents;
	}
	```
	***

