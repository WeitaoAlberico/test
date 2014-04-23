# Custom Report
### __Description__

  This represents a "kitchen sink" report to show the use of java report data source that runs a beanshell rule to get its data.

### __Files' Detail__

 1. **Task Definition xml File** - _TaskDef_Report.xml_
    
    The task definition specifies its parameters values. Such as executor, datasource, reference form.
 2. **Form xml File** - _Form_Report.xml_
    
    The form is used to collect additional information to configure the report.
 3. **Datasource Java File** - _DataSource_Report.java_
    
    The Java Datasource file is the executor of the report. It calls a rule which generates the data.
 4. **Rule xml File** - _Rule_Report.xml_
    
    The rule is to generate data for the report.

