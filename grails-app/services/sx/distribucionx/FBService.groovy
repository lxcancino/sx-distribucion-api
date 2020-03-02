package sx.distribucionx

import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import groovy.sql.Sql

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;



@Transactional
class FBService {

    @Autowired
    @Qualifier('dataSource')
    def dataSource



    Firestore db

    def configConnect(){
      
        FileInputStream serviceAccount = new FileInputStream("grails-app/conf/servicedeskpapel-firebase-adminsdk-fv2f7-7f1bf062e3.json");

        def apiK="AIzaSyCTiDju61dKy9aofcqt9pdo7q2dYw6U0vE"

        FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        //.setApiKey(apiK)
         //.setApplicationId(servicedeskpapel)
        .setDatabaseUrl("https://servicedeskpapel.firebaseio.com")
        .build();

        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
        /* 
            FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .setDatabaseUrl("https://<DATABASE_NAME>.firebaseio.com/")
            .build();

            FirebaseApp.initializeApp(options);
         */

        /*  
             FirebaseOptions options = new FirebaseOptions.Builder()
            .setApiKey('AIzaSyCTiDju61dKy9aofcqt9pdo7q2dYw6U0vE')
            .setApplicationId(servicedeskpapel)
            .setDatabaseUrl(firebaseBaseUrl)
            .build();
         */
         //FirebaseApp.initializeApp(options);
       // 

         // [START fs_initialize]
        //db = FirestoreOptions.getDefaultInstance().getService();
    // [END fs_initialize]
   
    }



}
