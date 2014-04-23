import net.sf.jasperreports.engine.JRException;

import net.sf.jasperreports.engine.JRField;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;

import sailpoint.api.SailPointFactory;

import sailpoint.object.*;

import sailpoint.reporting.datasource.JavaDataSource;

import sailpoint.task.Monitor;

import sailpoint.tools.GeneralException;



import java.util.HashMap;

import java.util.List;

import java.util.Map;


public class ReportRuleDataSource implements JavaDataSource {



    private static Logger log = Logger.getLogger(ReportRuleDataSource.class);

    List<Map<String, Object>> rows = null;

    int currentRowIndex;

    Map<String, Object> currentRow = null;

    private QueryOptions baseQueryOptions = null;



    LiveReport liveReport = null;



    Monitor monitor = null;

    Rule dataSourceRule = null;

    Attributes<String, Object> reportAttributes = null;

    SailPointContext context = null;

    List<Sort> sorts = null;







    @Override

    public void initialize(SailPointContext sailPointContext,

                           LiveReport liveReport,

                           Attributes<String, Object> stringObjectAttributes,

                           String s, List<Sort> sorts) throws GeneralException {



        log.debug("initialize");

        this.liveReport = liveReport;

        this.reportAttributes = stringObjectAttributes;

        this.sorts = sorts;

        initVariables();

    }



    private void initVariables() throws GeneralException {

        log.debug("initVariables");



        this.context = SailPointFactory.getCurrentContext();

        this.currentRowIndex = 0;

        this.baseQueryOptions = new QueryOptions();





        String ruleName = this.reportAttributes.getString("dataSourceRule");

        String reportDefName = this.reportAttributes.getString("reportDefName");

        TaskDefinition reportDef = this.context.getObject(TaskDefinition.class, reportDefName);

        TaskItemDefinition taskDefinitionParent = reportDef.getParentRoot();

        String taskDefinitionParentName = taskDefinitionParent.getName();

        Rule dataSourceRuleByName = this.context.getObject(Rule.class, taskDefinitionParentName);

        if(dataSourceRuleByName != null) {

            log.debug("Loading rule based on the task definition name");

            this.dataSourceRule = dataSourceRuleByName;

        }else{

            log.debug("Loading rule from the configuration.");

            this.dataSourceRule = this.context.getObject(Rule.class, ruleName);

        }

        if (this.dataSourceRule == null) {

            log.error("Error loading the rule:" + ruleName);

            throw new GeneralException("The dataSourceRule cannot be loaded. Check report config and rule.");

        } else {

            log.debug("Loaded the rule:" + ruleName);

        }

    }



    private void loadReportData() throws JRException {

        try {

            log.debug("loadReportData");

            Logger logRule = Logger.getLogger("xom.rule." + this.dataSourceRule.getName());

            Map<String, Object> ruleArgs = new HashMap<String, Object>();

            ruleArgs.put("liveReport", this.liveReport);

            ruleArgs.put("reportAttributes", this.reportAttributes);

            ruleArgs.put("monitor", this.monitor);

            ruleArgs.put("sorts", this.sorts);

            ruleArgs.put("log", logRule);

            ruleArgs.put("context", context);

            log.debug("going to run the dataSource rule");

            this.rows = (List<Map<String, Object>>) this.context.runRule(this.dataSourceRule, ruleArgs);

            log.debug("Successfully ran the dataSource rule");

        } catch (Exception ex) {

            throw new JRException("Error running the report");

        }

    }



    @Override

    public void setLimit(int i, int i2) {

        log.debug("setLimit");

        //To change body of implemented methods use File | Settings | File Templates.

    }



    @Override

    public int getSizeEstimate() throws GeneralException {

        log.debug("getSizeEstimate");

        return 0;  //To change body of implemented methods use File | Settings | File Templates.

    }



    @Override

    public QueryOptions getBaseQueryOptions() {

        log.debug("getBaseQueryOptions");

        return this.baseQueryOptions;

    }



    @Override

    public String getBaseHql() {

        log.debug("getBaseHql");

        return null;  //To change body of implemented methods use File | Settings | File Templates.

    }



    @Override

    public Object getFieldValue(String s) throws GeneralException {

        log.debug("getFieldValue:" + s);

        Object returnValue = null;

        try {

            returnValue = this.currentRow.get(s);

        } catch (Exception ex) {

        }

        return returnValue;

    }



    @Override

    public void setMonitor(Monitor monitor) {

        log.debug("setMonitor");

        this.monitor = monitor;

    }



    @Override

    public void close() {

        log.debug("close");

        //To change body of implemented methods use File | Settings | File Templates.

    }



    @Override

    public boolean next() throws JRException {

        log.debug("next");

        if (this.rows == null) {

            loadReportData();

            this.currentRowIndex = 0;

        }

        if (this.currentRowIndex < this.rows.size()) {

            currentRow = this.rows.get(this.currentRowIndex);

            currentRowIndex++;

            return true;

        } else {

            return false;

        }

    }



    @Override

    public Object getFieldValue(JRField jrField) throws JRException {

        log.debug("getFieldValue:" + jrField);

        String name = jrField.getName();

        try {

            return getFieldValue(name);

        } catch (GeneralException e) {

            throw new JRException(e);

        }



    }

}