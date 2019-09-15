
package com.guangmushikong.lbi.service;

import com.guangmushikong.lbi.dao.UserDataDao;
import com.guangmushikong.lbi.model.UserDataDO;
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
public class UserDataService {
    @Autowired
    UserDataDao userDataDao;

    @Value("${spring.img.path}")
    String imgPath;

    public List<UserDataDO> listUserData(long projectId){
        return userDataDao.listUserData(projectId);
    }
    public void addUserData(UserDataDO customVO){
        userDataDao.addUserData(customVO);
    }

    public void saveUserData(UserDataDO customVO){
        UserDataDO customVO2=userDataDao.getUserData(customVO.getUuid());
        if(customVO2!=null){
            userDataDao.updateUserData(customVO);
        }else {
            userDataDao.addUserData(customVO);
        }
    }

    public void updateUserData(UserDataDO customVO){
        userDataDao.updateUserData(customVO);
    }

    public void delUserData(String uuid,long projectId){
        userDataDao.delUserData(uuid,projectId);
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
