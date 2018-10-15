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
public class OptionPriceChangeRequestApplier_Invalid_Request_Test {

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

    // 실패했을때 핸들하는 메서드가 두개다 .
    // 대칭적이지 않다. 프로덕 이슈 와 옵션 이슈가 별개로 보인다.
    // 실패는 있고 성공은 없네 ?
    @Test
    public void testFail1() {

        assertThat(dut.save(null)).isFalse();

        verify(requestFailHandler).handle(null, REQUEST_MUST_BE_NOT_NULL);
    }

    @Test
    public void testFail2() {
        OptionPriceChangeRequest request = productEnv
                .initRequest()
                .build();

        assertThat(dut.save(request)).isFalse();

        verify(requestFailHandler).handle(request, OPTION_IS_EMPTY);
    }

    @Test
    public void testFail3() {
        OptionPriceChangeRequest request = productEnv
                .initRequest()
                .changePrice(1000L, 10000)
                .build();

        assertThat(dut.save(request)).isFalse();

        verify(requestFailHandler).handle(request, 1000L, NOT_FOUND_OPTION);
    }

    @Test
    public void testFail4() {
        OptionPriceChangeRequest request = productEnv
                .initRequest()
                .changePrice(1000L, 10000)
                .changePrice(2000L, 10000)
                .build();

        assertThat(dut.save(request)).isFalse();

        verify(requestFailHandler).handle(request, 1000L, NOT_FOUND_OPTION);
        verify(requestFailHandler).handle(request, 2000L, NOT_FOUND_OPTION);
    }

    @Test
    public void testFail5() {
        OptionPriceChangeRequest request = productEnv
                .initRequest()
                .changePrice(8L, 10000)
                .changePrice(9L, 10000)
                .changePrice(1000L, 10000)
                .build();

        assertThat(dut.save(request)).isTrue();

        verify(requestFailHandler).handle(request, 1000L, NOT_FOUND_OPTION);
    }

}