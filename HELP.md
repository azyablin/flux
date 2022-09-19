# Getting Started

Проект демонстрирует стриминговую передачу данных БД с по REST

## Modules

- *common*
    - содержит общий функционал: конфигурацию, инициализациую БД и т.п.
    - [code](https://github.com/azyablin/flux/tree/main/common)
- *model*
    - содержит общии модели и сущности
    - [code](https://github.com/azyablin/flux/tree/main/common)
- *hibernate*
    - содержит серверную часть обмена с использованием hibernate
    - [code](https://github.com/azyablin/flux/tree/main/hibernate)
- *mybatis*
    - содержит серверную часть обмена с использованием mybatis
    - [code](https://github.com/azyablin/flux/tree/main/mybatis)
- *client*
    - клиентская часть spring, использующая WebClient
    - [code](https://github.com/azyablin/flux/tree/main/client)

### Основные моменты

* [hibernate stateless session](https://github.com/azyablin/flux/blob/main/hibernate/src/main/java/com/magnit/flux/hibernate/dao/HibernateFluxResultProducer.java#L36)
* [hibernate stream + Flux](https://github.com/azyablin/flux/blob/main/hibernate/src/main/java/com/magnit/flux/hibernate/dao/HibernateFluxResultProducer.java#L40)
* [REST + hibernate stream](https://github.com/azyablin/flux/blob/main/hibernate/src/main/java/com/magnit/flux/hibernate/controller/HibernateOperationController.java#L27)
* [RSocket + hibernate stream](https://github.com/azyablin/flux/blob/main/hibernate/src/main/java/com/magnit/flux/hibernate/controller/HibernateOperationController.java#L32)
* [объявление курсора Mybatis для отображения отношения ManyToMany](https://github.com/azyablin/flux/blob/main/mybatis/src/main/resources/mapper/operation-detail-mapper.xml#L11)
* [использование сессии MyBatis, позволяющее избежать долгоиграющих транзакций](https://github.com/azyablin/flux/blob/main/mybatis/src/main/java/com/magnit/flux/mybatis/dao/MyBatisFluxResultProducer.java#L40)
* [MyBatis cursor + Flux](https://github.com/azyablin/flux/blob/main/mybatis/src/main/java/com/magnit/flux/mybatis/dao/MyBatisFluxResultProducer.java#L42)
* [REST + MyBatis cursor](https://github.com/azyablin/flux/blob/main/mybatis/src/main/java/com/magnit/flux/mybatis/controller/MyBatisOperationController.java#L26)
* [блокирующее ограничение скорости чтения, используя обратное давление и Backet4j](https://github.com/azyablin/flux/blob/main/client/src/main/java/com/magnit/flux/client/service/RateLimitService.java#L30)
* [неблокирующее ограничение скорости чтения, используя обратное давление и Backet4j](https://github.com/azyablin/flux/blob/main/client/src/main/java/com/magnit/flux/client/service/RateLimitService.java#L49)


