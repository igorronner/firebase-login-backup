# Firebase Login And Backup

> Toolkit for building a onboarding quickly with Firebase plus backup solution.

Getting Started
--------
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

**Open SignUp:**

```java
  IRLoginBackup.openSignUp(activity);
```
<img src="/prints/device-2018-05-11-173553.png" alt="test image size" height="40%" width="40%">


**Open SignIn:**

```java
  IRLoginBackup.openSigIn(activity);
```
<img src="/prints/device-2018-05-11-173728.png" alt="test image size" height="40%" width="40%">

**Check logged:**

```java
  IRLoginBackup.isLogged(context);
```


**Logout Dialog:**

```java
  IRLoginBackup.logoutDialog(..);
```

**Backup DB:**

```java
  IRLoginBackup.backup(activity);
```

**Open Restore backup:**

```java
  IRLoginBackup.openRestoreBackup(activity);
```

