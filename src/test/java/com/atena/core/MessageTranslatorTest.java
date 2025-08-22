package com.atena.core;

import static com.atena.exceptions.MessageTranslator.loadMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class MessageTranslatorTest {

  @Test
  void testMessage() {
    final String message = loadMessage("msg.test");
    assertEquals("test", message);
  }

  @Test
  void testMessageNotFound() {
    final String message = loadMessage("does not exist");
    assertEquals("does not exist", message);
  }

  @Test
  void testMessageFailMessageWithoutParams() {
    assertThrows(IllegalStateException.class, () -> loadMessage("msg.test", 1));
  }

  @Test
  void testMessageFailTooManyParams() {
    assertThrows(IllegalStateException.class, () -> loadMessage("msg.test.params", 1, 2));
  }

  @Test
  void testMessageFailNoParams() {
    assertThrows(IllegalStateException.class, () -> loadMessage("msg.test.params"));
  }

  @Test
  void testMessageParams() {
    final String message = loadMessage("msg.test.params", 1);
    assertEquals("test 1", message);
  }
}
