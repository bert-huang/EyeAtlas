package nz.ac.aucklanduni.eyeatlas.util;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;


@Config(emulateSdk=18)
@RunWith(MyRobolectricTestRunner.class)
public class TestFileReader {

    private ShadowApplication mockApplication;
    private Context mockContext;

    @BeforeClass
    public static void oneTimeSetup() {}

    @Before
    public void setUp() {
        mockApplication = Robolectric.getShadowApplication();
        Assert.assertNotNull(mockApplication);
        mockContext = mockApplication.getApplicationContext();
        Assert.assertNotNull(mockContext);
    }

    @Test
    public void testFileReader() throws IOException {
        String result = FileReader.getFileContent("test/testreader.txt", mockContext);
        assertEquals("this is a test", result);
    }

}

//    private final static String FILE_PATH = "/Users/bert/Documents/projects/EyeAtlas/app/src/main/assets/test/testreader.txt";
//    private final static String FILE_PATH = "test/testreader.txt";
//
//    @Test
//    public void testFileReader() throws IOException {
//        Mockito.when(FileReader.fileStreamProvider(Mockito.anyString(), Mockito.any(MockContext.class))).thenAnswer(new Answer<FileInputStream>() {
//            @Override
//            public FileInputStream answer(InvocationOnMock invocation) throws Throwable {
//                Object[] args = invocation.getArguments();
//                return new FileInputStream((String) args[0]);
//            }
//        });
//
//        String result = FileReader.getFileContent(FILE_PATH, new MockContext());
//        assertThat(result, is("this is a test"));
//    }
