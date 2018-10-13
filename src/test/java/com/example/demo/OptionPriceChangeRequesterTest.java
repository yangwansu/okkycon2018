package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.demo.ProductStatus.LIVED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class OptionPriceChangeRequesterTest {

    private OptionPriceChangeRequester optionPriceChangeRequester;

    @Mock private ProductRepository productRepository;
    @Mock private OptionRepository optionRepository;
    @Mock private RequestSuccessHandler requestSuccessHandler;
    @Mock private RequestFailHandler requestFailHandler;
    @Mock private RequestHistoryRepository requestHistoryRepository;

    private ProductEnv productEnv;

    @Before
    public void setUp()  {
        optionPriceChangeRequester = new OptionPriceChangeRequester(
                productRepository,
                optionRepository,
                requestHistoryRepository,
                requestSuccessHandler,
                requestFailHandler
        );

        //이렇게 까지 노력 할 필요가 머있을까?
        productEnv = initProduct(
                1L,
                LIVED,
                option(8L, 1000),
                option(9L, 3000));

        given(productRepository.findById(1L)).willReturn(productEnv.getProduct());
        given(optionRepository.findByOptionIdIn(anyListOf(Long.class))).willReturn(productEnv.getOptions());
    }

    private ProductEnv initProduct(long productId, ProductStatus status, Function<Long,Option> ... foptions) {
        return new ProductEnv(
                product(productId,status),
                Arrays.stream(foptions).map(f -> f.apply(productId)).collect(Collectors.toList()));
    }

    @Test
    public void testSend() {

        OptionPriceChangeRequest request = request(
                1L,
                change(8L, 1000, 2000),
                change(9L, 3000, 4000));

        assertThat(optionPriceChangeRequester.send(request)).isTrue();

        assertThat(productEnv.getProduct().getStatus()).isEqualTo(ProductStatus.REQUEST);
        assertThat(option(1L, 8L).getPrice()).isEqualTo(2000L);
        assertThat(option(1L, 9L).getPrice()).isEqualTo(4000L);

        verify(requestSuccessHandler).handle(request);
        verify(productRepository).save(productEnv.getProduct());
        verify(optionRepository).save(option(1L, 8L));
        verify(optionRepository).save(option(1L, 9L));
        verify(requestHistoryRepository).save(request);

        verifyNoMoreInteractions(requestFailHandler);
    }

    private Option option(Long productId, Long optionId) {
        return productEnv.getOptions().stream().filter(it-> it.getProductId().equals(productId) && it.getId().equals(optionId)).findFirst().orElse(null);
    }

    private Function<Long, Option> option(Long optionId, int price) {
        return (productId) -> option(productId, optionId, price);
    }

    private Option option(Long productId, Long optionId, int price) {
        Option option1 = new Option();
        option1.setId(optionId);
        option1.setProductId(productId);
        option1.setPrice(price);
        return option1;
    }

    private Product product(Long productId, ProductStatus status) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        return product;
    }

    private RequestOption change(Long optionId, int before, int after) {
        RequestOption requestOption2 = new RequestOption();
        requestOption2.setOptionId(optionId);
        requestOption2.setBefore(before);
        requestOption2.setAfter(after);
        return requestOption2;
    }

    private OptionPriceChangeRequest request(Long productId, RequestOption ... requestOption) {
        OptionPriceChangeRequest request = new OptionPriceChangeRequest();
        request.setProductId(productId);
        request.setTransactionId(1L);
        request.setOptions(new ArrayList<>(Arrays.asList(requestOption)));
        return request;
    }
}