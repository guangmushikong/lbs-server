
package com.guangmushikong.lbi.service;

import com.guangmushikong.lbi.dao.CustomDataSetDao;
import com.guangmushikong.lbi.model.CustomVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/*************************************
 * Class Name: CustomDataSetService
 * Description:〈CustomDataSetService〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Service
@Slf4j
public class CustomDataSetService {
    @Autowired
    CustomDataSetDao customDataSetDao;

    @Value("${spring.img.path}")
    String imgPath;

    public List<CustomVO> listCustomDataSet(long projectId){
        return customDataSetDao.listCustomDataSet(projectId);
    }
    public void addCustomDataSet(CustomVO customVO){
        customDataSetDao.addCustomData(customVO);
    }

    public void updateCustomDataSet(CustomVO customVO){
        customDataSetDao.updateCustomData(customVO);
    }

    public String saveJpg(long projectId,String fileName,byte[] bytes)throws IOException {
        String uploadFolder=imgPath+File.separator+projectId;
        File dir=new File(uploadFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath=dir+File.separator+fileName;
        File outFile=new File(filePath);
        FileCopyUtils.copy(bytes,outFile);
        return filePath;
    }

    public byte[] getFile(long projectId,String fileName)throws IOException{
        String filePath=imgPath+File.separator+projectId+File.separator+fileName;
        File file=new File(filePath);
        return FileCopyUtils.copyToByteArray(file);
    }
}
