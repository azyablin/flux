package com.magnit.flux.common.service;


import com.magnit.flux.common.repository.CustomerRepository;
import com.magnit.flux.common.repository.OperationRepository;
import com.magnit.flux.common.repository.ProductRepository;
import com.magnit.flux.model.entity.Customer;
import com.magnit.flux.model.entity.Operation;
import com.magnit.flux.model.entity.Product;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class InitDataService {

    private static final int OPERATION_RECORD_LIMIT = 100000;

    private static final int CUSTOMER_RECORD_LIMIT = 100;

    private static final int PRODUCT_RECORD_LIMIT = 100;

    private static final Random random = new Random();

    private final OperationRepository operationRepository;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    @PostConstruct
    public void init() {
        if (customerRepository.count() == 0) {
            fillCustomer();
        }
        if (productRepository.count() == 0) {
            fillProduct();
        }
        if (operationRepository.count() == 0) {
            fillOperation();
        }
    }

    private void fillCustomer() {
        Flux.range(0, CUSTOMER_RECORD_LIMIT)
            .map(i -> Customer.builder().name("Customer " + i).build())
            .subscribe(customerRepository::save);
    }

    private void fillProduct() {
        Flux.range(0, PRODUCT_RECORD_LIMIT)
            .map(i -> Product.builder().name("Product " + i).build())
            .subscribe(productRepository::save);
    }

    private void fillOperation() {
        Supplier<List<Product>> productSupplier = () -> new Random().ints(5, 1, 8)
            .boxed().distinct()
            .map(productId -> Product.builder().id((long) productId).build())
            .collect(Collectors.toList());
        List<Customer> customers = (List<Customer>) customerRepository.findAll();
        Flux.range(0, OPERATION_RECORD_LIMIT)
            .map(i -> Operation.builder()
                .totalSum((long) random.nextInt(100))
                .customer(customers.get(random.nextInt(customers.size() - 1)))
                .products(productSupplier.get())
                .build())
            .buffer(1000)
            .subscribe(operationRepository::saveAll);
    }

}
