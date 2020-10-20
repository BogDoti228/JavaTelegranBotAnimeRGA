package tagsTests;

import org.junit.Assert;
import org.junit.Test;

import tagsConroller.NameCreator;


public class TestCreator {
    private final String defaultName = NameCreator.defaultName;

    @Test
    public void testWhenFullStringIsTag() {
        var str = "hello";
        var expected = "hello";
        var actual = NameCreator.createNameWithTags(str);
        Assert.assertEquals(
                expected,
                actual
        );
    }

    @Test
    public void testWhenTwoDefaultTags() {
        var str = "hello world";
        var expected = "hello_world";
        var actual = NameCreator.createNameWithTags(str);
        Assert.assertEquals(
                expected,
                actual
        );
    }

    @Test
    public void testWhenTagContainsNumbers() {
        var str = "he110";
        var expected = "he110";
        var actual = NameCreator.createNameWithTags(str);
        Assert.assertEquals(
                expected,
                actual
        );
    }

    @Test
    public void testAcceptRussianLanguage() {
        var str = "привет";
        var expected = "привет";
        var actual = NameCreator.createNameWithTags(str);
        Assert.assertEquals(
                expected,
                actual
        );
    }

    @Test
    public void testWhenTagContainsUnderlines() {
        var str = "h_e__llo";
        var expected = "h__e____llo";
        var actual = NameCreator.createNameWithTags(str);
        Assert.assertEquals(
                expected,
                actual
        );
    }

    @Test
    public void testReturnDefaultWhenZeroAcceptedSymbolsInInput() {
        var str = ",.\\   .!!^=&^##$^&?";
        Assert.assertEquals(NameCreator.createNameWithTags(str), defaultName);
    }

    @Test
    public void testReturnDefaultWhenUnderlineAtTheBeginning() {
        var str = "_hello";
        Assert.assertEquals(NameCreator.createNameWithTags(str), defaultName);
    }

    @Test
    public void testReturnDefaultWhenUnderlineAtTheEnd() {
        var str = "hello_";
        Assert.assertEquals(NameCreator.createNameWithTags(str), defaultName);
    }

    @Test
    public void testReturnedValueOnLowerCase(){
        var str = "Hello WORLD";
        var expected = "hello_world";
        Assert.assertEquals(
                expected,
                NameCreator.createNameWithTags(str)
        );
    }


    @Test
    public void testIgnoreExtraSpaces(){
        var str = "hello     world";
        var expected = "hello_world";
        Assert.assertEquals(
                expected,
                NameCreator.createNameWithTags(str)
        );
    }

    @Test
    public void testUltraHardDifficultyOmegaHardSituation() {
        var str = "he11lo_world,+=123   abc;length";
        var expected = "123_abc_he11lo__world_length";
        Assert.assertEquals(expected, NameCreator.createNameWithTags(str));
    }
}