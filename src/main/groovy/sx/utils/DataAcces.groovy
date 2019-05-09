package sx.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import groovy.sql.Sql
import org.springframework.jdbc.datasource.DriverManagerDataSource

class DataAcces{
    
    def dataSourceResolve(ip, bd, user, password){

        def urlJdbc='jdbc:mysql://'+ip+"/"+bd
        def driverManagerDs = new DriverManagerDataSource()
        driverManagerDs.driverClassName = "com.mysql.jdbc.Driver"
        driverManagerDs.url = urlJdbc
        driverManagerDs.username = user
        driverManagerDs.password = password

        return driverManagerDs

    }

    def dataSourceResolve(urlJdbc, user, password){

        def driverManagerDs = new DriverManagerDataSource()
        driverManagerDs.driverClassName = "com.mysql.jdbc.Driver"
        driverManagerDs.url = urlJdbc
        driverManagerDs.username = user
        driverManagerDs.password = password

        return driverManagerDs

    }

    def getSql(dataSource){
        def sql=new Sql(dataSource)
        return sql
    }


}
