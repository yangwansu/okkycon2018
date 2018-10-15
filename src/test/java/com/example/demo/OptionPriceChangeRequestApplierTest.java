package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.example.demo.ProductEnv.option;
import static com.example.demo.ProductStatus.LIVED;
import static com.example.demo.RequestFailReasons.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OptionPriceChangeRequestApplierTest {

    @Mock private ProductRepository productRepository;
    @Mock private OptionRepository optionRepository;
    @Mock private RequestFailHandler requestFailHandler;
    @Mock private RequestHistoryRepository requestHistoryRepository;

    private OptionPriceChangeRequestApplier dut;

    private ProductEnv productEnv;

    @Before
    public void setUp()  {

        dut = new OptionPriceChangeRequestApplier(
                productRepository,
                optionRepository,
                requestHistoryRepository,
                requestFailHandler);

        //이렇게 까지 노력 할 필요가 머있을까?
        productEnv = ProductEnv.initRequest(
                1L,
                LIVED,
                option(8L, 1000),
                option(9L, 3000));

        given(productRepository.findById(1L)).willReturn(productEnv.getProduct());
        given(optionRepository.findByOptionIdIn(anyListOf(Long.class))).willReturn(productEnv.getOptions());
    }

    //드디어 세이브 자체에만 집중할수 있게 되었다.
    //하지만 테스트 이름이 아직도 무엇을 테스트하는지 잘 표현하지 못하고 있음
    @Test
    public void testSend() {
        OptionPriceChangeRequest request = productEnv.initRequest()
                .changePrice(8L, 2000)
                .changePrice(9L, 4000)
                .build();

        assertThat(dut.save(request)).isTrue();

        assertThat(productEnv.getProduct().getStatus()).isEqualTo(ProductStatus.REQUEST);
        assertThat(productEnv.option(1L, 8L).getPrice()).isEqualTo(2000L);
        assertThat(productEnv.option(1L, 9L).getPrice()).isEqualTo(4000L);

        verify(productRepository).save(productEnv.getProduct());
        verify(optionRepository).save(productEnv.option(1L, 8L));
        verify(optionRepository).save(productEnv.option(1L, 9L));
        verify(requestHistoryRepository).save(request);
    }
}