package org.pjj.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.joda.time.DateTime;
import org.pjj.oss.service.OssService;
import org.pjj.oss.utils.ConstantPropertiesUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author PengJiaJun
 * @Date 2022/2/26 18:28
 */
@Service
public class OssServiceImpl implements OssService {

    @Override
    public String uploadFileAvatar(MultipartFile file) {
        String endpoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessSecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;
        String fileName = file.getOriginalFilename();

        //1. 在文件名里添加 随机唯一值 防止文件名重复(重复文件名会覆盖)
        //uuid生成的值是 ajfi-ja34-adji-... 这里我们不想要横杠-, 则替换为空
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        fileName = uuid + fileName;//adjiatai3r3w文件名.jpg

        //2. 把文件按照日期进行分类 年/月/日/时
        // 2019/11/12/10/adjiatai3r3w文件名.jpg
        String datePath = new DateTime().toString("yyyy/MM/dd/HH");//获取当前日期并转为指定格式返回(2022/2/27/01)

        //拼接
        fileName = datePath + "/" + fileName;

        //创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessSecret);
        InputStream inputStream = null;//上传文件流
        try{
            inputStream = file.getInputStream();

            //第一个参数: bucket名称, 第二个参数: 上传到oss的文件路径(/img/ii/tu.png) 和 文件的名称(tu.png)
            //第三个参数: 一个 输入流, 可从该流中读取到需要上传的文件 最后写出至oss
            ossClient.putObject(bucketName, fileName, inputStream);//调用oss方式实现上传

            ossClient.shutdown();//关闭ossClient

//            https://guli-edu-2022-2-26.oss-cn-beijing.aliyuncs.com/PiKaQiu.gif
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;

            return url;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != inputStream){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 删除 oss上的文件(包括: 视频, 头像, 封面)
     *
     * @param filePath
     * @return
     */
    @Override
    public boolean deleteFile(String filePath) {

// yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = ConstantPropertiesUtils.END_POINT;
// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
// 填写Bucket名称。
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;
// 填写文件完整路径。文件完整路径中不能包含Bucket名称。
// 此处Bucket名称为: guli-edu-2022-2-26
// 传来的filePath为: https://guli-edu-2022-2-26.oss-cn-beijing.aliyuncs.com/2022/03/10/11/0803f55cc86b4013b6a1c15d5f9511a9cs1.jpg
// 正确的路径:  2022/03/10/11/0803f55cc86b4013b6a1c15d5f9511a9cs1.jpg
        String objectName = filePath.substring(55);//由于前面的网址是固定的所以每次都按这样substring即可切出路径

// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(bucketName, objectName);

// 关闭OSSClient。
        ossClient.shutdown();

        return true;
    }

}
