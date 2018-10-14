package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.example.demo.ProductEnv.option;
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
        productEnv = ProductEnv.initRequest(
                1L,
                LIVED,
                option(8L, 1000),
                option(9L, 3000));

        given(productRepository.findById(1L)).willReturn(productEnv.getProduct());
        given(optionRepository.findByOptionIdIn(anyListOf(Long.class))).willReturn(productEnv.getOptions());
    }

    @Test
    public void testSend() {
        OptionPriceChangeRequest request = productEnv.initRequest()
                .changePrice(8L, 2000)
                .changePrice(9L, 4000)
                .build();

        assertThat(optionPriceChangeRequester.send(request)).isTrue();

        assertThat(productEnv.getProduct().getStatus()).isEqualTo(ProductStatus.REQUEST);
        assertThat(productEnv.option(1L, 8L).getPrice()).isEqualTo(2000L);
        assertThat(productEnv.option(1L, 9L).getPrice()).isEqualTo(4000L);

        verify(requestSuccessHandler).handle(request);
        verify(productRepository).save(productEnv.getProduct());
        verify(optionRepository).save(productEnv.option(1L, 8L));
        verify(optionRepository).save(productEnv.option(1L, 9L));
        verify(requestHistoryRepository).save(request);

        verifyNoMoreInteractions(requestFailHandler);
    }
}