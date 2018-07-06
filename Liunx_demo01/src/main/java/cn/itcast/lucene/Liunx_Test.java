package cn.itcast.lucene;

import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import javax.management.Query;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Liunx_Test {

    @Test
    public void testCreateIndex() throws IOException {
        //指定索引库目录
        String url = "C:/a_concordance";
        FSDirectory directory = FSDirectory.open(Paths.get(url));
        //指定分词器
        Analyzer analyzer = new StandardAnalyzer();
        // Analyzer analyzer = new HanLPAnalyzer();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //创建索引库核心写对象
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //加载数据源文件
        File dir = new File("C:\\a_data_origin");
        //读取文件
        File[] files = dir.listFiles();
        for (File file : files) {
            //文件名
            String fileName = file.getName();
            //文件内容
            String fileConent = FileUtils.readFileToString(file);
            String path = file.getPath();
            long fileSize = FileUtils.sizeOf(file);
            //创建文件名域  第一个参数：域的名称  第二个参数：域的内容  //第三个参数：是否存储
            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);
            Field fileConentField = new TextField("fileConent", fileConent, Field.Store.YES);
            //文件路径域（不分析、不索引、只存储）
            Field pathField = new StoredField("path", path);
            Field fileSizeField = new StoredField("fileSize", fileSize);

            //创建document对象
            Document document = new Document();
            document.add(fileNameField);
            document.add(fileConentField);
            document.add(pathField);
            document.add(fileSizeField);
            //创建索引，并写入索引库
            //indexWriter.addDocument(document);


/*          TermQuery query = new TermQuery(new Term("fileName", "springmvc"));
            indexWriter.deleteDocuments(query);*/

            indexWriter.commit();
        }
        //关闭资源
        indexWriter.close();
    }


}
