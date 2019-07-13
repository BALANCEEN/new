//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.activiti.engine.ProcessEngine;
//import org.activiti.engine.ProcessEngineConfiguration;
//import org.activiti.engine.RepositoryService;
//import org.activiti.engine.TaskService;
//import org.activiti.engine.history.HistoricProcessInstance;
//import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
//import org.activiti.engine.runtime.Execution;
//import org.activiti.engine.runtime.ProcessInstance;
//import org.activiti.engine.task.Task;
//
///**
// * activiti工作流测试
// *注：此类未导入jar包
// * @author zk
// * @date 2019年7月12日
// */
//public class ActivitiTest {
//
//    private static ProcessEngine processEngine;
//
//    static {
//        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration
//                .createStandaloneProcessEngineConfiguration();
//        StandaloneProcessEngineConfiguration engineConfig = (StandaloneProcessEngineConfiguration) ProcessEngineConfiguration
//                .createStandaloneProcessEngineConfiguration();
//        System.out.println(engineConfig);
//        // 设置数据库连接属性
//        engineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
//        engineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3308/test?createDatabaseIfNotExist=true"
//                + "&useUnicode=true&characterEncoding=utf8");
//        engineConfiguration.setJdbcUsername("username");
//        engineConfiguration.setJdbcPassword("password");
//        engineConfiguration.setDatabaseSchemaUpdate("true");// 自动创建所需数据库表
//        processEngine = engineConfiguration.buildProcessEngine();
//    }
//
//    /** 部署定义流程(启动时加载一次即可) */
//    public static void defineProEngin() {
//        RepositoryService repositoryService = processEngine.getRepositoryService();
//        // 加载一个工作流
//        repositoryService.createDeployment()
//                .addClasspathResource("com/venustech/tsoc/cupid/workrun/bpmn/subProcess.bpmn")
//                .addClasspathResource("com/venustech/tsoc/cupid/workrun/bpmn/subProcess.png").name("测试222")// 工作流名称
//                .deploy();
//    }
//
//    /** 启动一个子流程 */
//    public static void startProEngin() {
//        List<String> list = new ArrayList<>();
//        list.add("a1");
//        list.add("a2");
//        Map<String, Object> map = new HashMap<>();
//        //list的大小即为子流程个数，在Collection中设置，不设置即不包含子流程
//        map.put("list", list);
//        // 通过流程实例key启动流程(bpmn中的id)
//        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("myProcess", map);
//        System.out.println("流程实例ID：" + processInstance.getProcessInstanceId());
//    }
//
//    /** 执行流程 */
//    public static void runProEngin(String proInstatceId) {
//        TaskService taskService = processEngine.getTaskService();
//        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(proInstatceId).singleResult();
//        System.out.println(task.getId());
//        Map<String, Object> map = new HashMap<>();
//        map.put("result", 1);
//        setVariables(proInstatceId);
//        // 完成一个流程节点（通过taskId），可以通过map向后面传值
//        taskService.complete(task.getId(), map);
//    }
//
//    /** set参数 */
//    public static void setVariables(String proInstatceId) {
//        ProcessInstance instance = processEngine.getRuntimeService().createProcessInstanceQuery()
//                .processInstanceId(proInstatceId).singleResult();
//        System.out.println("流程实例ID：" + instance.getId());
//        Map<String, Object> map = new HashMap<>();
//        map.put("申请人", "小明");
//        map.put("时间", new Date());
//        processEngine.getRuntimeService().setVariables(instance.getId(), map);
//    }
//
//    /** get参数 */
//    public static void getVariables(String proInstatceId) {
//        Map<String, Object> map = processEngine.getRuntimeService().getVariables(proInstatceId);
//        map.forEach((k, v) -> {
//            System.out.println(k + "--" + v);
//        });
//    }
//
//    /**
//     * 审核人完成批注
//     *
//     * @param id
//     * @return
//     */
//    public static void completeComment(String proInstatceId) {
//        // 根据流程id查询流程实例
//        Execution execution = processEngine.getRuntimeService().createProcessInstanceQuery()
//                .processInstanceId(proInstatceId).singleResult();
//        TaskService taskService = processEngine.getTaskService();
//        Task task = taskService.createTaskQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
//        // 添加批注信息
//        taskService.addComment(task.getId(), proInstatceId, "评论信息");
//        // 完成审核（result表示两条的不同路线）
//        Map<String, Object> map = new HashMap<>();
//        map.put("result", 0);
//        taskService.complete(task.getId(), map);
//    }
//
//    /**
//     * 查询子流程执行id 子流程流程实例id相同，执行id不同
//     */
//    public static void subQueryExeId(String proInstatceId) {
//        List<Task> list = processEngine.getTaskService().createTaskQuery().processInstanceId(proInstatceId).list();
//        for (Task task : list) {
//            System.out.println("流程执行id：" + task.getExecutionId());
//            System.out.println("流程任务id：" + task.getId());
//            // 通过taskid和属性名获取参数
//            Object variable = processEngine.getTaskService().getVariable(task.getId(), "user");
//            System.out.println(variable);
//            // 根据taskid执行子流程
//            Map<String, Object> map = new HashMap<>();
//            processEngine.getTaskService().complete(task.getId(), map);
//        }
//    }
//
//    public static void deleteProInstance(String proInstatceId) {
//        // 一般在流程实例中设置businessKey为唯一标识，可以使用 class名称+id值 唯一表示，用作流程实例和业务逻辑的联系点
//        // HistoricProcessInstance hpi =
//        // processEngine.getHistoryService().createHistoricProcessInstanceQuery()
//        // .processInstanceBusinessKey("").singleResult();
//        HistoricProcessInstance hpi = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
//                .processInstanceId(proInstatceId).singleResult();
//        String processInstanceId = hpi.getId(); // 流程实例ID
//        // 判断该流程实例是否结束，未结束和结束两者删除表的信息是不一样的。
//        ProcessInstance pi = processEngine.getRuntimeService().createProcessInstanceQuery()//
//                .processInstanceId(processInstanceId)// 使用流程实例ID查询
//                .singleResult();
//        if (pi == null) {
//            // 该流程实例已经完成了
//            processEngine.getHistoryService().deleteHistoricProcessInstance(processInstanceId);
//        } else {
//            // 该流程实例未结束的
//            processEngine.getRuntimeService().deleteProcessInstance(processInstanceId, "");
//            processEngine.getHistoryService().deleteHistoricProcessInstance(processInstanceId);// (顺序不能换)
//        }
//    }
//
//    public static void main(String[] args) {
//        // 以下所需参数均为流程实例ID，在项目中应该使用businessKey为唯一标识。
//        defineProEngin();// 部署流程
//        startProEngin();// 启动一个流程实例
//        runProEngin("15005");// 运行流程实例，完成流程节点
//        subQueryExeId("20005");// 子流程实例查询&完成子流程
//        deleteProInstance("5");// 删除流程实例
//        setVariables("22501");// 设置参数
//        getVariables("22501");// 获取参数
//    }
//}
