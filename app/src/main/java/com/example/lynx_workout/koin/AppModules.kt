package com.example.lynx_workout.koin

//import com.example.lynx_workout.data.remote.ApiInterface
//import com.example.lynx_workout.data.remote.RemoteDataSource
import com.example.lynx_workout.data.repository.Repository
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

object AppModules {

    private val retrofitModule: Module = module {
        single {
//            val interceptor = HttpLoggingInterceptor()
//            interceptor.level = HttpLoggingInterceptor.Level.BODY
//
//            val client = OkHttpClient.Builder()
//            client.addInterceptor(interceptor)
//
//            Retrofit.Builder()
//                .baseUrl("https://lynx-workout.firebaseio.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .client(client.build())
//                .build()
//                .create(ApiInterface::class.java)
        }
    }

    private val remoteDataSourceModule: Module = module {
//        single { RemoteDataSource() }
    }

    private val repoModule: Module = module {
        single { Repository() }
    }

    val appModules =
        listOf(retrofitModule, repoModule, remoteDataSourceModule)
}