package com.example.some.es;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregator;
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.missing.Missing;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentile;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 
 * es查询测试，版本1.7.2
 * @author zk
 * @date 2019年7月15日
 */
public class EsQuery {
	
	static TransportClient client;
	
	static final String type="event";
	
	static {
		 //1:通过 setting对象来指定集群配置信息
		Settings setting = ImmutableSettings.settingsBuilder()
			.put("cluster.name", "cupid-es")//指定集群名称
			//.put("client.transport.sniff", true)//启动嗅探功能
			.build();
		
		//2：创建客户端  通过setting来创建，若不指定则默认链接的集群名为elasticsearch
		client = new TransportClient(setting);                        
		TransportAddress transportAddress = new InetSocketTransportAddress("192.168.19.175", 8300);
		client.addTransportAddresses(transportAddress);
		
		//3：查看集群信息
	    ImmutableList<DiscoveryNode> connectedNodes = client.connectedNodes();
	    for(DiscoveryNode node : connectedNodes)
	    {
//	    	System.out.println(node.getHostAddress());
	    }
	}
	
	/**
	 * 4种方式产生JSON格式的文档(document)：
	 * 手动方式，使用原生的byte[]或者String
	 * 使用Map方式，会自动转换成与之等价的JSON
	 * 使用第三方库来序列化beans，如Jackson
	 * 使用内置的帮助类 XContentFactory.jsonBuilder()
	 */
	public static void createDocument() throws Exception {
		// ①手动方式，使用原生的byte[]或者String
		String json = "{" +"\"user\":\"fendo\"," +"\"postDate\":\"2013-01-30\"," +
				"\"message\":\"Hell word\"" +"}";
		// ②使用Map方式，会自动转换成与之等价的JSON
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user","kimchy");
		map.put("postDate",new Date());
		map.put("message","trying out Elasticsearch");
		// ③使用第三方库来序列化beans，如Jackson
		Student stu = new Student();
		stu.setAge(7);
		stu.setName("小dd");
		
		//不指定id会自动生成一个，如AWvzaXwpNJqde9AlgQXd
		IndexResponse indexResponse = client.prepareIndex("aa", type,"22")
				//ElasticSearch已经使用了jackson，可以直接使用它把javabean转为json
				.setSource(new ObjectMapper().writeValueAsBytes(stu))
				.get();
		System.out.println(indexResponse.getVersion()); 
	}
	
	/**
	 * 删除一条数据同过id
	 */
	public static void deleteById() {
		DeleteResponse deleteResponse = client.prepareDelete("aa", type, "1").get();
		System.out.println(deleteResponse.getVersion());
	}
	
	/**
	 * 更新文档通过XContentFactory
	 * 还有很多种方式
	 */
	public static void updateOne() throws IOException {
		XContentBuilder doc = XContentFactory.jsonBuilder()
				.startObject()
				.field("name","xiaowangba")
				.endObject();
		UpdateResponse updateResponse = client.prepareUpdate("aa", type, "6")
				.setDoc(doc).get();
		System.out.println(updateResponse.getVersion());
	}
	
	/**
	 * 通过id查询一条数据
	 */
	public static void queryOne() throws JsonParseException, JsonMappingException, IOException {
		GetResponse getResponse = client.prepareGet("aa", type, "6")
				//operationThreaded设置为  true是在不同的线程里执行此次操作
				.setOperationThreaded(false)
				.get();
		System.out.println(getResponse.getSourceAsString());
		// 疑问：转不回javabean;解决：把内部类改成静态内部类，否则找不到构造方法
		Student stu = new ObjectMapper().readValue(getResponse.getSourceAsString(), Student.class);
		System.out.println(stu.getName());
	}
	
	/**
	 * 查询索引条数
	 */
	public static void queryCount() {
		long count = client.prepareCount("event20170707").get().getCount();
		System.out.println(count);
	}
	
	/**
	 * 多索引多id查询
	 */
	public static void queryBySome() {
		MultiGetResponse multiGetResponse = client.prepareMultiGet()
				.add("aa", type, "2","3")// 一个索引多个id
				.add("bb", type ,"5")// 另外索引
				.get();
		for (MultiGetItemResponse multiGetItemResponse : multiGetResponse) {
			GetResponse response = multiGetItemResponse.getResponse();
			if (response!=null) {
				System.out.println(response.getSourceAsString());
			}
		}
	}
	
	/**
	 * 通过条件查询
	 * 组合查询：setQuery后面会覆盖前面
     * must(QueryBuilders) :   AND
     * mustNot(QueryBuilders): NOT
     * should:               : OR
	 */
	public static void searchByCnd() {
		SearchResponse searchResponse = client.prepareSearch("aa","event20170707")// 多索引查询
				.setTypes(type,"event")// 多类型查询
				//***对于非文本类型相当于=,对于文本类型：（*查询内容不分词，直接与分词表=比较，有则返回like文档）
				//***例：小a,小b,小c  小a--》结果为空，a--》结果为小a，小--》结果为所有
//				.setQuery(QueryBuilders.termQuery("user", "fendo"))
//				.setQuery(QueryBuilders.rangeQuery("age").from(2).to(12))//范围查询 bwtten...and...
//				.setPostFilter(FilterBuilders.rangeFilter("age").from(2).to(12).includeLower(true))//过滤查询 bwtten...and...
//				.setScroll(new TimeValue(5000))//游标 设定时间内持续放回结果
				//匹配分词查询，（*查询内容分词，分别与分词表=比较，有则返回like文档取并结果集）
				//例：小a,小b,小c 小a--》结果为所有，a--》结果为小a，小--》结果为所有
//				.setQuery(QueryBuilders.matchQuery("ceventname", "漏洞预警"))
//				.setQuery(QueryBuilders.wildcardQuery("user", "f*o"))//通配符查询,不可分词
//				.setQuery(QueryBuilders.queryStringQuery("sysadmin"))// 解析查询字符串--查询时转义特殊字符
				.setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("ceventname", "漏洞预警")).should(QueryBuilders.wildcardQuery("user", "f*o")))
				.setFrom(0)//从第几条开始，同setSize一起使用
				.setSize(100)//设置查询结果条数，否则只有10条
				.addHighlightedField("user")
				.setHighlighterPreTags("<h1>")
	            .setHighlighterPostTags("</h1>")
				.get();
		SearchHits hits = searchResponse.getHits();
		for (SearchHit searchHit : hits) {
			System.out.println(searchHit.getSourceAsString());
		}
	}
	
	/**
	 * 聚合查询Aggregation
	 */
	public static void aggregationByCnd() {
		// ①定义聚合查询，max取别名，field聚合字段名
		MaxBuilder field = AggregationBuilders.max("aa").field("age");
		SearchResponse searchResponse = client.prepareSearch("aa")// 多索引查询
				.setTypes(type)// 多类型查询
//				.setQuery(QueryBuilders.matchQuery("name", "z"))
				.setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", "z"))
						.should(QueryBuilders.matchQuery("name", "w")))
				// ②在查询中添加聚合内容
				.addAggregation(field)
				.setSize(100)//设置查询结果条数，否则只有10条
				.get();
		// ③获取聚合结果，注意：直接返回对应聚合的结果接口
		Max max = searchResponse.getAggregations().get("aa");
		System.out.println(max.getValue());
		SearchHits hits = searchResponse.getHits();
		for (SearchHit searchHit : hits) {
			System.out.println(searchHit.getSourceAsString());
		}
	}
	
	/**
	 * 扩展聚合查询Aggregation（多个聚合结果）
	 */
	public static void extendedAggregationByCnd() {
		// ①定义聚合查询，max取别名，field聚合字段名
		ExtendedStatsBuilder field = AggregationBuilders.extendedStats("aa").field("age");
		SearchResponse searchResponse = client.prepareSearch("aa")// 多索引查询
				.setTypes(type)// 多类型查询
//				.setQuery(QueryBuilders.matchQuery("name", "z"))
				.setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", "z"))
						.should(QueryBuilders.matchQuery("name", "w")))
				// ②在查询中添加聚合内容
				.addAggregation(field)
				.setSize(100)//设置查询结果条数，否则只有10条
				.get();
		// ③获取聚合结果，注意：直接返回对应聚合的结果接口
		ExtendedStats extendedStats = searchResponse.getAggregations().get("aa");
		System.out.println(extendedStats.getAvg());
		System.out.println(extendedStats.getMax());
		System.out.println(extendedStats.getSum());
		SearchHits hits = searchResponse.getHits();
		for (SearchHit searchHit : hits) {
			System.out.println(searchHit.getSourceAsString());
		}
	}
	
	/**
	 * 结果数量聚合查询Aggregation（多个聚合结果）
	 */
	public static void countAggregationByCnd() {
		// ①定义聚合查询，max取别名，field聚合字段名
		ValueCountBuilder field = AggregationBuilders.count("aa").field("age");
		SearchResponse searchResponse = client.prepareSearch("aa")// 多索引查询
				.setTypes(type)// 多类型查询
//				.setQuery(QueryBuilders.matchQuery("name", "z"))
				.setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", "z"))
						.should(QueryBuilders.matchQuery("name", "w")))
				// ②在查询中添加聚合内容
				.addAggregation(field)
				.setSize(100)//设置查询结果条数，否则只有10条
				.get();
		// ③获取聚合结果，注意：直接返回对应聚合的结果接口
		ValueCount count = searchResponse.getAggregations().get("aa");
		System.out.println(count.getValue());
		SearchHits hits = searchResponse.getHits();
		for (SearchHit searchHit : hits) {
			System.out.println(searchHit.getSourceAsString());
		}
	}
	
	/**
	 * 百分比聚合查询Aggregation（多个聚合结果）
	 */
	public static void percentilesAggregationByCnd() {
		// ①定义聚合查询，max取别名，field聚合字段名
		PercentilesBuilder field = AggregationBuilders.percentiles("aa").field("age");
		SearchResponse searchResponse = client.prepareSearch("aa")// 多索引查询
				.setTypes(type)// 多类型查询
//				.setQuery(QueryBuilders.matchQuery("name", "z"))
				.setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", "z"))
						.should(QueryBuilders.matchQuery("name", "w")))
				// ②在查询中添加聚合内容
				.addAggregation(field)
				.setSize(100)//设置查询结果条数，否则只有10条
				.get();
		// ③获取聚合结果，注意：直接返回对应聚合的结果接口
		Percentiles percentiles = searchResponse.getAggregations().get("aa");
		for (Percentile percentile : percentiles) {
			System.out.println(percentile.getPercent()+"--"+percentile.getValue());
		}
		SearchHits hits = searchResponse.getHits();
		for (SearchHit searchHit : hits) {
			System.out.println(searchHit.getSourceAsString());
		}
	}
	
	/**
	 * 总结查询
	 */
	public static void commentQuery() {
//		MissingBuilder field = AggregationBuilders.missing("agg").field("adfasdf");//不包含某个字段的聚合
//		基于某个field，该 field 内的每一个【唯一词元】为一个桶，并计算每个桶内文档个数。默认返回顺序是按照文档个数多少排序。
//		TermsBuilder field2 = AggregationBuilders.terms("aaa").field("name");
		TermsBuilder field2 = AggregationBuilders.terms("aaa")
				.field("name").order(Terms.Order.count(true));//数量升序//Terms.Order.term(true)字典升序
		SearchResponse searchResponse = client.prepareSearch("aa")// 多索引查询
				.setTypes(type)// 多类型查询
				.setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("name", "z"))
						.should(QueryBuilders.matchQuery("name", "w")))
				.addAggregation(field2)
				.setSize(100)//设置查询结果条数，否则只有10条
				.addSort(SortBuilders.fieldSort("age").order(SortOrder.DESC))//排序
				.get();
//		Terms terms = searchResponse.getAggregations().get("aaa");
//		System.out.println(terms.getBuckets().size());
//		List<Bucket> list = terms.getBuckets();//返回字段分词后桶的各词数量
//		for (Bucket bucket : list) {
//			System.out.println(bucket.getKey()+"--"+bucket.getDocCount());
//		}
		Terms terms = searchResponse.getAggregations().get("aaa");
		System.out.println(terms.getBuckets().size());
		List<Bucket> list = terms.getBuckets();//返回字段分词后桶的各词数量
		for (Bucket bucket : list) {
			System.out.println(bucket.getKey()+"--"+bucket.getDocCount());
		}
		SearchHits hits = searchResponse.getHits();
		for (SearchHit searchHit : hits) {
			System.out.println(searchHit.getSourceAsString());
		}
	}
	
//	Term Query（项查询）   termsQuery("tags", "blue", "pill"); 
//	Range Query（范围查询）  rangeQuery("price").from(5)   gte()/from()/includeLower(true)
//	Exists Query（存在查询）  existsQuery("name");
//	Prefix Query（前缀查询）  prefixQuery("brand", "heine");
//	Wildcard Query（通配符查询）  wildcardQuery("user", "k?mc*");  单字符通配符（？）和多字符通配符（*）,避免使用
//	Regexp Query（正则表达式查询）   regexpQuery("name.first", "s.*y");
//	Fuzzy Query（模糊查询）fuzzyQuery("name", "kimzhy");
//	Type Query（类型查询）  typeQuery("my_type");
//	Ids Query（ID查询）  idsQuery("my_type", "type2").addIds("1", "4", "100");//type可选

//	bool 查询 QueryBuilder qb = boolQuery().must(termQuery("content", "test1"))
//			.mustNot(termQuery("content", "test2")) //must not query
//			.should(termQuery("content", "test3")) // should query
//			.filter(termQuery("content", "test5))
	
	public static void main(String[] args) throws Exception {
//		createDocument();
//		queryOne();
//		deleteById();
//		updateOne();
//		queryBySome();
//		queryCount();
//		searchByCnd();
//		aggregationByCnd();
//		extendedAggregationByCnd();
//		countAggregationByCnd();
//		percentilesAggregationByCnd();
		commentQuery();
	}
	
	static class Student{
		private String name;
		private int age;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
	}
}
