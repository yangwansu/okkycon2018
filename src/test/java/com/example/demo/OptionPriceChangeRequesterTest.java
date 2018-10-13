package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    Product product;
    List<Option> options;

    @Before
    public void setUp()  {
        optionPriceChangeRequester = new OptionPriceChangeRequester(
                productRepository,
                optionRepository,
                requestHistoryRepository,
                requestSuccessHandler,
                requestFailHandler
        );

        product = product(1L, LIVED);
        options = options(
                option(1L, 8L, 1000),
                option(1L, 9L, 3000));

        given(productRepository.findById(1L)).willReturn(product);
        given(optionRepository.findByOptionIdIn(anyListOf(Long.class))).willReturn(options);
    }

    @Test
    public void testSend() {

        OptionPriceChangeRequest request = request(
                1L,
                change(8L, 1000, 2000),
                change(9L, 3000, 4000));

        assertThat(optionPriceChangeRequester.send(request)).isTrue();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.REQUEST);
        assertThat(option(1L, 8L).getPrice()).isEqualTo(2000L);
        assertThat(option(1L, 9L).getPrice()).isEqualTo(4000L);

        verify(requestSuccessHandler).handle(request);
        verify(productRepository).save(product);
        verify(optionRepository).save(option(1L, 8L));
        verify(optionRepository).save(option(1L, 9L));
        verify(requestHistoryRepository).save(request);

        verifyNoMoreInteractions(requestFailHandler);
    }

    private Option option(Long productId, Long optionId) {
        return options.stream().filter(it-> it.getProductId().equals(productId) && it.getId().equals(optionId)).findFirst().orElse(null);
    }

    private List<Option> options(Option ... option) {
        return new ArrayList<>(Arrays.asList(option));
    }

    private Option option(Long productId, Long id, int price) {
        Option option1 = new Option();
        option1.setId(id);
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