package com.example.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class OptionPriceChangeRequesterTest {

    private OptionPriceChangeRequester dut;

    @Mock private OptionPriceChangeRequestApplier optionPriceChangeRequestApplier;
    @Mock private RequestSuccessHandler requestSuccessHandler;
    @Mock private RequestFailHandler requestFailHandler;
    @Mock private OptionPriceChangeRequest request;
    @Mock private OptionPriceChangeResponse response;
    @Before
    public void setUp()  {

        dut = new OptionPriceChangeRequester(
                optionPriceChangeRequestApplier,
                requestSuccessHandler,
                requestFailHandler
        );

        given(optionPriceChangeRequestApplier.save(request)).willReturn(response);
    }

    @Test
    public void when_request_is_success() {
        given(response.hasFails()).willReturn(false);

        assertThat(dut.send(request)).isTrue();

        verify(requestSuccessHandler).handle(request);
        verifyNoMoreInteractions(requestFailHandler);
    }

    @Test
    public void when_request_is_fail() {
        given(response.hasFails()).willReturn(true);

        assertThat(dut.send(request)).isFalse();

        verifyNoMoreInteractions(requestSuccessHandler);
        verify(requestFailHandler).handle(request, response);
    }
}