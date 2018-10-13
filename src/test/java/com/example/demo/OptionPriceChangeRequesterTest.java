package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class OptionPriceChangeRequesterTest {

    @Mock private ProductRepository productRepository;
    @Mock private OptionRepository optionRepository;
    @Mock private RequestSuccessHandler requestSuccessHandler;
    @Mock private RequestFailHandler requestFailHandler;
    @Mock private RequestHistoryRepository requestHistoryRepository;

    @Test
    public void testSend() {
        OptionPriceChangeRequester optionPriceChangeRequester = new OptionPriceChangeRequester(
                productRepository,
                optionRepository,
                requestHistoryRepository,
                requestSuccessHandler,
                requestFailHandler
        );

        OptionPriceChangeRequest request = new OptionPriceChangeRequest();
        request.setProductId(1L);
        request.setTransactionId(1L);

        RequestOption requestOption1 = new RequestOption();
        requestOption1.setOptionId(8L);
        requestOption1.setBefore(1000);
        requestOption1.setAfter(2000);

        RequestOption requestOption2 = new RequestOption();
        requestOption2.setOptionId(9L);
        requestOption2.setBefore(3000);
        requestOption2.setAfter(4000);

        List<RequestOption> requestOptions = new ArrayList<>();
        requestOptions.add(requestOption1);
        requestOptions.add(requestOption2);

        request.setOptions(requestOptions);

        Product product = new Product();
        product.setId(1L);
        product.setStatus(ProductStatus.LIVED);

        Option option1 = new Option();
        option1.setId(8L);
        option1.setProductId(1L);
        option1.setPrice(1000);

        Option option2 = new Option();
        option2.setId(9L);
        option2.setProductId(1L);
        option2.setPrice(3000);

        List<Option> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);

        given(productRepository.findById(1L)).willReturn(product);
        given(optionRepository.findByOptionIdIn(anyListOf(Long.class))).willReturn(options);

        assertThat(optionPriceChangeRequester.send(request)).isTrue();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.REQUEST);
        assertThat(option1.getPrice()).isEqualTo(2000L);
        assertThat(option2.getPrice()).isEqualTo(4000L);

        verify(requestSuccessHandler).handle(request);
        verify(productRepository).save(product);
        verify(optionRepository).save(option1);
        verify(optionRepository).save(option2);
        verify(requestHistoryRepository).save(request);

        verifyNoMoreInteractions(requestFailHandler);
    }
}