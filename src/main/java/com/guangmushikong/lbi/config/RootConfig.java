
package com.guangmushikong.lbi.config;

import lombok.extern.slf4j.Slf4j;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/*************************************
 * Class Name: RootConfig
 * Description:〈启动配置〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Configuration
@Slf4j
public class RootConfig {
    @Value("${dem.gujiao}")
    String dem_gujiao;
    @Value("${dem.jingzhuang}")
    String dem_jingzhuang;

    @Bean(name = "coverageGujiao")
    public GridCoverage2D initGujiao()throws Exception{
        GeoTiffReader tifReader1 = new GeoTiffReader(dem_gujiao);
        GridCoverage2D coverage = tifReader1.read(null);
        log.info("init coverage_gujiao");
        return coverage;
    }

    @Bean(name = "coverageJingzhuang")
    public GridCoverage2D initJingzhuang()throws Exception{
        GeoTiffReader tifReader2 = new GeoTiffReader(dem_jingzhuang);
        GridCoverage2D coverage = tifReader2.read(null);
        log.info("init coverage_jingzhuang");
        return coverage;
    }
}
