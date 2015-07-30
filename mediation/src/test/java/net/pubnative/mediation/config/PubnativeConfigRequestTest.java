package net.pubnative.mediation.config;

import net.pubnative.mediation.model.PubnativeConfigModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by davidmartin on 28/07/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class PubnativeConfigRequestTest
{
    @Test
    public void test_creationNotNull()
    {
        PubnativeConfigRequest request = new PubnativeConfigRequest();
        assertThat(request).isNotNull();
    }

    @Test
    public void test_callbacksWithValidListener()
    {
        PubnativeConfigRequest requestSpy = spy(PubnativeConfigRequest.class);
        PubnativeConfigRequestListener listenerMock = mock(PubnativeConfigRequestListener.class);

        requestSpy.listener = listenerMock;
        requestSpy.invokeStart();

        PubnativeConfigModel configModelMock = mock(PubnativeConfigModel.class);
        requestSpy.invokeLoaded(configModelMock);

        Exception exceptionMock = mock(Exception.class);
        requestSpy.invokeFailed(exceptionMock);

        verify(listenerMock, times(1)).onRequestStarted(requestSpy);
        verify(listenerMock, times(1)).onRequestLoaded(requestSpy, configModelMock);
        verify(listenerMock, times(1)).onRequestFailed(requestSpy, exceptionMock);
    }

    @Test
    public void test_callbacksWithNullListener()
    {
        PubnativeConfigRequest requestSpy = spy(PubnativeConfigRequest.class);
        PubnativeConfigRequestListener listenerMock = mock(PubnativeConfigRequestListener.class);

        requestSpy.invokeStart();

        PubnativeConfigModel configModelMock = mock(PubnativeConfigModel.class);
        requestSpy.invokeLoaded(configModelMock);

        Exception exceptionMock = mock(Exception.class);
        requestSpy.invokeFailed(exceptionMock);

        verify(listenerMock, never()).onRequestStarted(requestSpy);
        verify(listenerMock, never()).onRequestLoaded(requestSpy, configModelMock);
        verify(listenerMock, never()).onRequestFailed(requestSpy, exceptionMock);
    }

    // TODO: Create tests
}
