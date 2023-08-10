package com.ray.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class,args);
        //"id"	"hosname"	"hoscode"	"api_url"	"sign_key"	"contacts_name"	"contacts_phone"	"status"	"create_time"	"update_time"	"is_deleted"
        //"6"	"北京协和医院"	"1000_0"	"http://localhost:9998"	"674c4139707728439a6510eae12deea9"	"22"	"22"	"1"	"9/12/2020 09:33:46"	"12/12/2020 14:28:35"	"0"
    }

}
