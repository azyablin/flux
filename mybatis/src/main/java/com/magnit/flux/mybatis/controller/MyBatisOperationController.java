package com.magnit.flux.mybatis.controller;

import com.magnit.flux.model.entity.Operation;
import com.magnit.flux.mybatis.dao.MyBatisFluxResultProducer;
import com.magnit.flux.mybatis.mapper.OperationMapper;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class MyBatisOperationController {

    private final SqlSessionFactory sqlSessionFactory;

    private final ObjectFactory<MyBatisFluxResultProducer<Operation>> streamResultProducerObjectFactory;

    @SneakyThrows
    @GetMapping(path = "/test")
    public Long test() {
        AtomicLong counter = new AtomicLong(0L);
        try (
            SqlSession sqlSession = sqlSessionFactory.openSession();
            Cursor<Operation> cursor =
                sqlSession.getMapper(OperationMapper.class).getAllOperations()
        ) {
            cursor.forEach(foo -> {
                counter.incrementAndGet();
            });
        }
        return counter.get();
    }

    @GetMapping(path = "/operations-stream", produces = "application/stream+json")
    public Flux<Operation> getOperationsStream() {
        return getOperationFlux();
    }

    private Flux<Operation> getOperationFlux() {
        Function<SqlSession, Cursor<Operation>> cursorFunction = sqlSession -> sqlSession
            .getMapper(OperationMapper.class).getAllOperations();

        Mono<MyBatisFluxResultProducer<Operation>> streamResultExecutorMono = Mono
            .just(streamResultProducerObjectFactory.getObject());
        return Flux.usingWhen(streamResultExecutorMono,
            se -> se.execute(cursorFunction),
            MyBatisFluxResultProducer::close);
    }

}
