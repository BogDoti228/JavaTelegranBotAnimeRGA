package tagsTests;

import org.junit.Assert;
import org.junit.Test;
import tagsConroller.TagsParser;


public class TestParser {
    @Test
    public void testEmptyStringShouldReturnEmptyArray(){
        var str = "";
        Assert.assertArrayEquals(
                new String[0],
                TagsParser.parseTagsFromFileName(str)
        );
    }

    @Test
    public void testWhenFullStringIsATag(){
        var str = "hello";
        var expected = new String[] {str};
        Assert.assertArrayEquals(
                expected,
                TagsParser.parseTagsFromFileName(str)
        );
    }

    @Test
    public void testWhenOneTagWithUnderline(){
        var str = "hello__world";
        var expected = new String[] {"hello_world"};
        Assert.assertArrayEquals(
                expected,
                TagsParser.parseTagsFromFileName(str)
        );
    }

    @Test
    public void testWhenStringContainsExtension(){
        var str = "image.jpg";
        var expected = new String[] {"image"};
        Assert.assertArrayEquals(
                expected,
                TagsParser.parseTagsFromFileName(str)
        );
    }

    @Test
    public void testWhenNumbersInTag(){
        var str = "he110";
        var expected = new String[] { "he110" };
        Assert.assertArrayEquals(
                expected,
                TagsParser.parseTagsFromFileName(str)
        );
    }

    @Test
    public void testWhenTwoDefaultTags(){
        var str = "hello_world";
        var expected = new String[] {"hello", "world"} ;
        Assert.assertArrayEquals(
                expected,
                TagsParser.parseTagsFromFileName(str)
        );
    }
}
