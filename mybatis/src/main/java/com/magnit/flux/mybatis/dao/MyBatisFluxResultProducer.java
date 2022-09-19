package com.magnit.flux.mybatis.dao;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Использование mybatis для стриминга
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class MyBatisFluxResultProducer<T> {

    private final SqlSessionFactory sqlSessionFactory;

    private Cursor<T> cursor;

    private SqlSession sqlSession;

    /**
     * @param cursorFunction фунция для продюсирования кусросра
     * @return Flux соответсвующего типа
     */
    public Flux<T> execute(Function<SqlSession, Cursor<T>> cursorFunction) {
        Optional.ofNullable(sqlSession).ifPresent(e -> {
            throw new RuntimeException("Cursor already open");
        });

        sqlSession = sqlSessionFactory.openSession();
        cursor = cursorFunction.apply(sqlSession);
        return Flux.fromStream(StreamSupport.stream(cursor.spliterator(), false));
    }

    @SneakyThrows
    public Mono<Void> close() {
        if (cursor.isOpen()) {
            cursor.close();

        }
        sqlSession.close();
        return Mono.empty();
    }

}