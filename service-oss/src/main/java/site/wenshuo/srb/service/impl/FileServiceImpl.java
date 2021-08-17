package site.wenshuo.srb.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectResult;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import site.wenshuo.srb.service.FileService;
import site.wenshuo.srb.util.OssProperties;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public void remove(String url) {
        String host = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/";
        String objectName = url.substring(host.length());
        OSS build = new OSSClientBuilder().build(OssProperties.ENDPOINT, OssProperties.KEY_ID, OssProperties.KEY_SECRET);
        build.deleteObject(OssProperties.BUCKET_NAME, objectName);
        build.shutdown();

    }

    @Override
    public String upload(InputStream inputStream, String module, String fileName) {
        OSS build = new OSSClientBuilder().build(OssProperties.ENDPOINT, OssProperties.KEY_ID, OssProperties.KEY_SECRET);
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        String time = new DateTime().toString("yyyy/MM/dd");
        String random = UUID.randomUUID().toString();
        String key = module + "/" + time + "/" + random + extension;
        boolean b = build.doesBucketExist(OssProperties.BUCKET_NAME);
        if (b) {
            PutObjectResult examplebucket = build.putObject(OssProperties.BUCKET_NAME, key, inputStream);
            ResponseMessage response = examplebucket.getResponse();

        } else {
            build.createBucket(OssProperties.BUCKET_NAME);
            build.setBucketAcl(OssProperties.BUCKET_NAME, CannedAccessControlList.PublicRead);
        }
        build.shutdown();
        return "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/" + key;
    }
}
