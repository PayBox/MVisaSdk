**PayBoxMvisa SDK (Android)**


PayBox SDK Android - это библиотека позволяющая проводить оплату Mvisa через API PayBox. 
Библиотека работает совместно с [SDK_Android_input](https://github.com/PayBox/SDK_Android_input)


**Установка:**

1. Добавьте репозитории Jitpack в ваш build.gradle в конец репозиториев:
```
repositories {
    // ...
    maven { url "https://jitpack.io" }
}
```
<br><br>
2. Добавьте зависимости:
```
dependencies {
    implementation 'com.github.PayBox:MVisaSdk:0.9.1@aar'
    implementation 'com.github.PayBox:SDK_Android_input:1.0.3.3@aar'
    implementation 'com.google.android.gms:play-services-vision:17.0.2'
}
```
**Инициализация SDK:**
```
        PBHelper.Builder builder = new PBHelper.Builder(appContext,secretKey,merchantId);
```


Выбор платежной системы:
```
        builder.setPaymentSystem(Constants.PBPAYMENT_SYSTEM.EPAYWEBKZT);
```


Выбор валюты платежа:
```
        builder.setPaymentCurrency(Constants.CURRENCY.KZT);
```


Дополнительная информация пользователя, если не указано, то выбор будет предложен на сайте платежного гейта:
```
        builder.setUserInfo(email, 8777*******);
```


Активация автоклиринга:
```
        builder.enabledAutoClearing(true);
```

Для активации режима тестирования:
```
        builder.enabledTestMode(true);
```


Для передачи информации от платежного гейта:
```
        builder.setFeedBackUrl(checkUrl,resultUrl,refundUrl,captureUrl, REQUEST_METHOD);
```

Время (в секундах) в течение которого платеж должен быть завершен, в противном случае, при проведении платежа, PayBox откажет платежной системе в проведении (мин. 300 (5 минут), макс. 604800 (7 суток), по умолчанию 300):
```
        builder.setPaymentLifeTime(300);
```


**Инициализация параметров:**
```
        builder.build();

```
**Работа с SDK:**


Для связи с SDK, имплементируйте в Activity -> “MVisaScanListener”:
1. В методе onResume() добавьте:
```
        @Override
        protected void onResume() {
            super.onResume();
            MVisaHelper.getInstance().setListener(this);
        }
```
2. В методе onDestroy():
```
        @Override
        protected void onDestroy() {
            super.onDestroy();
            MVisaHelper.getInstance().removeListener();
        }
```

**Для вызова сканера** 
```
        MVisaHelper.getInstance().initScan(context, userId);  
```
В ответ откроется "QR сканер", наведите камеру смартфона на QR MVisa, после сканирования "QR сканер" закроется и в вашем activity вызовется метод:
```
        public void onQrDetected(MVisa mVisa, ArrayList<Card> cards)
```
Во входных параметрах будет:
    1. mVisa - содержит сумму платежа, валюта платежа, имя мерчанта и номер мерчанта в системе MVisa;
    2. cards - содержит массив доступных карт пользователя, если пользователь не добавлял карты то вместо массива будет "null";

Данные параметры вы сможете отобразить на вашей странице "Подтверждения платежа"


**Подтверждение платежа**

После подтверждения платежа пользователем вызовите метод:
```        
        MVisaHelper.getInstance().initPayment(orderId, card, description, extraParams); //Если пользователь не добавлял карт то вместо "card" укажите null
```
1. Если пользователь не добавлял карт то в ответ откроется "WebView" с платежной страницей, после оплаты карта будет сохранена в системе PayBox.
2. Если пользователь добавил карту ранее то пройдет платеж по "Рекуррентному профилю".

После оплаты в вашем activity вызовется метод:
```
        public void onQrPaymentPaid(Response response)
```

В случае ошибки или неуспешного платежа вызовется метод:
```
        public void onQrError(Error error)
```

**Описание некоторых входных параметров**

1. orderId - Идентификатор платежа в системе продавца. Рекомендуется поддерживать уникальность этого поля.
2. amount - Сумма платежа
3. merchantId - Идентификатор продавца в системе PayBox. Выдается при подключении.
4. secretKey - Платежный пароль, используется для защиты данных, передаваемых системой PayBox магазину и магазином системе Paybox
5. userId - Идентификатор клиента в системе магазина продавца.
6. paymentId - Номер платежа сформированный в системе PayBox.
7. description - Описание товара или услуги. Отображается покупателю в процессе платежа.
8. extraParams - Дополнительные параметры продавца. Имена дополнительных параметров продавца должны быть уникальными. 
9. checkUrl - URL для проверки возможности платежа. Вызывается перед платежом, если платежная система предоставляет такую возможность. Если параметр не указан, то берется из настроек магазина. Если параметр установлен равным пустой строке, то проверка возможности платежа не производится.                                                                                                                                       возможности платежа не производится.
10. resultUrl - URL для сообщения о результате платежа. Вызывается после платежа в случае успеха или неудачи. Если параметр не указан, то берется из настроек магазина. Если параметр установлен равным пустой строке, то PayBox не сообщает магазину о результате платежа.
11. refundUrl - URL для сообщения об отмене платежа. Вызывается после платежа в случае отмены платежа на стороне PayBoxа или ПС. Если параметр не указан, то берется из настроек магазина.
12. captureUrl - URL для сообщения о проведении клиринга платежа по банковской карте. Если параметр не указан, то берется из настроек магазина.
13. REQUEST_METHOD - GET, POST или XML – метод вызова скриптов магазина checkUrl, resultUrl, refundUrl, captureUrl для передачи информации от платежного гейта.