# Firebase Login And Backup

> Toolkit for building a onboarding quickly with Firebase plus backup solution.

```java
IRLoginBackup.startInit("Database-Name-Here", getResources().getString(R.string.default_web_client_id))
                .setLogo(R.drawable.logo)
                .setLoginOptional(true)
                .build();
```

Download
--------

Step 1 - Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Step 2 - Add the dependency

```groovy
dependencies {
  implementation 'com.github.igorronner:firebase-login-backup:0.0.9.7'
}
```
