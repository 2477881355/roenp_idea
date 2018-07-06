package cn.itcast.lucene;

import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;


        /*
         * File类中的list()和listFiles()方法:
         *
         *  · list()方法是返回某个目录下的所有文件和目录的文件名，返回的是String数组
         *
         *  · listFiles()方法是返回某个目录下所有文件和目录的绝对路径，返回的是File数组
         *
         * */

public class CreateIndex {

    @Test
    public void testCreateIndex() throws IOException {

//   ·1     设置索引库目录
        String index = "C:\\a_concordance";
        FSDirectory fsDirectory = FSDirectory.open(Paths.get(index));

//         创建分词器 StandardAnalyzer
        Analyzer standardAnalyzer = new StandardAnalyzer();

        //   创建分词器
        //Analyzer standardAnalyzer = new HanLPAnalyzer();

        //        核心写对象 配置信息对象    参数：需要指定 分词器；
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(standardAnalyzer);

//         创建索引库    参数： 1.索引库路径    2.核心写对象
        IndexWriter indexWriter = new IndexWriter(fsDirectory, indexWriterConfig);

//      ·2   加载数据源文件
        File file = new File("C:\\a_data_origin");

//      获取所有数据源 文件
        File[] files = file.listFiles();

//    遍历 files并获得  文件名 和 文件内容
        assert files != null;
        for (File filer : files) {
            //文件名
            String fileName = filer.getName();
            //文件内容
            String fileConent = FileUtils.readFileToString(filer);
            //文件大小
            long fileSize = FileUtils.sizeOf(filer);
            //文件路径
            String path = filer.getPath();

            //创建文档对象
            Document document = new Document();

            //创建文件名域  第一个参数：域的名称  第二个参数：域的内容  //第三个参数：是否存储
            Field filedName = new TextField("fileName", fileName, Field.Store.YES);
            Field fieldConent = new TextField("fileConent", fileConent, Field.Store.YES);
            Field fieldPath = new StoredField("filePath", path);
            Field fieldSize = new StoredField("fileSize", fileSize);


            //为 document对象 注入参数
            document.add(filedName);
            document.add(fieldConent);
            document.add(fieldPath);
            document.add(fieldSize);


            //创建索引，并写入索引库
            indexWriter.addDocument(document);
            indexWriter.commit();
        }

        //关闭资源
        indexWriter.close();
    }


    private void executeQuery(Query query) throws IOException {
        //指定索引库目录
        String url = "C:\\a_concordance";
        FSDirectory directory = FSDirectory.open(Paths.get(url));
        IndexReader reader = DirectoryReader.open(directory);
        //创建查询对象
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("满足查询条件总记录数；" + topDocs.totalHits);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("文档id:" + scoreDoc.doc);
            System.out.println("文档得分:" + scoreDoc.score);

            //根据文档id查询文档对象
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println("文档名称:" + doc.get("fileName"));
            System.out.println("文档大小:" + doc.get("fileSize"));
            System.out.println("文档路径:" + doc.get("path"));
            System.out.println("文档内容:" + doc.get("fileConent"));
        }
    }

    @Test
    public void testMatchAllQueryIndex() throws IOException {

        //创建查询对象
        Query query = new MatchAllDocsQuery();

        //执行查询
        executeQuery(query);

    }

    /**
     * 组合查询
     */

    @Test
    public void testBoolean() throws IOException {

        Query query = new TermQuery(new Term("fileConent", "learn"));
        Query query1 = new TermQuery(new Term("fileConent", "spring"));
        BooleanClause booleanClause = new BooleanClause(query, BooleanClause.Occur.MUST);
        BooleanClause booleanClause1 = new BooleanClause(query, BooleanClause.Occur.MUST);

        BooleanQuery query2 = new BooleanQuery.Builder().add(booleanClause).add(booleanClause1).build();
        executeQuery(query2);

    }


    /**
     * 单字段域解析查询
     */
    @Test
    public void testQueryParser() throws Exception {

        //创建解析对象
        QueryParser parser = new QueryParser("fileConent", new HanLPAnalyzer());
        Query query = parser.parse("全文检索概念");
        //执行查询
        executeQuery(query);
    }

    
    /**
     * 多字段域解析查询
     */
    @Test
    public void testMultiFieldQueryParser() throws Exception, ParseException {

        //创建解析对象
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"fileConent", "fileName"}, new HanLPAnalyzer());
        Query query = parser.parse("apache 全文检索概念");
        //执行查询
        executeQuery(query);

    }

}