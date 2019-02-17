package net.minecraft.command;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.text.TextComponentTranslation;

public class TranslatableExceptionProvider implements BuiltInExceptionProvider {
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_LOW = new Dynamic2CommandExceptionType((p_208631_0_, p_208631_1_) -> {
      return new TextComponentTranslation("argument.double.low", p_208631_1_, p_208631_0_);
   });
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_HIGH = new Dynamic2CommandExceptionType((p_208627_0_, p_208627_1_) -> {
      return new TextComponentTranslation("argument.double.big", p_208627_1_, p_208627_0_);
   });
   private static final Dynamic2CommandExceptionType FLOAT_TOO_LOW = new Dynamic2CommandExceptionType((p_208624_0_, p_208624_1_) -> {
      return new TextComponentTranslation("argument.float.low", p_208624_1_, p_208624_0_);
   });
   private static final Dynamic2CommandExceptionType FLOAT_TOO_HIGH = new Dynamic2CommandExceptionType((p_208622_0_, p_208622_1_) -> {
      return new TextComponentTranslation("argument.float.big", p_208622_1_, p_208622_0_);
   });
   private static final Dynamic2CommandExceptionType INTEGER_TOO_LOW = new Dynamic2CommandExceptionType((p_208634_0_, p_208634_1_) -> {
      return new TextComponentTranslation("argument.integer.low", p_208634_1_, p_208634_0_);
   });
   private static final Dynamic2CommandExceptionType INTEGER_TOO_HIGH = new Dynamic2CommandExceptionType((p_208630_0_, p_208630_1_) -> {
      return new TextComponentTranslation("argument.integer.big", p_208630_1_, p_208630_0_);
   });
   private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType((p_208633_0_) -> {
      return new TextComponentTranslation("argument.literal.incorrect", p_208633_0_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.quote.expected.start"));
   private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.quote.expected.end"));
   private static final DynamicCommandExceptionType READER_INVALID_ESCAPE = new DynamicCommandExceptionType((p_208635_0_) -> {
      return new TextComponentTranslation("parsing.quote.escape", p_208635_0_);
   });
   private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType((p_208629_0_) -> {
      return new TextComponentTranslation("parsing.bool.invalid", p_208629_0_);
   });
   private static final DynamicCommandExceptionType READER_INVALID_INT = new DynamicCommandExceptionType((p_208625_0_) -> {
      return new TextComponentTranslation("parsing.int.invalid", p_208625_0_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.int.expected"));
   private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType((p_208626_0_) -> {
      return new TextComponentTranslation("parsing.double.invalid", p_208626_0_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.double.expected"));
   private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType((p_208623_0_) -> {
      return new TextComponentTranslation("parsing.float.invalid", p_208623_0_);
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.float.expected"));
   private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.bool.expected"));
   private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType((p_208632_0_) -> {
      return new TextComponentTranslation("parsing.expected", p_208632_0_);
   });
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType(new TextComponentTranslation("command.unknown.command"));
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType(new TextComponentTranslation("command.unknown.argument"));
   private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType(new TextComponentTranslation("command.expected.separator"));
   private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType((p_208628_0_) -> {
      return new TextComponentTranslation("command.exception", p_208628_0_);
   });

   public Dynamic2CommandExceptionType doubleTooLow() {
      return DOUBLE_TOO_LOW;
   }

   public Dynamic2CommandExceptionType doubleTooHigh() {
      return DOUBLE_TOO_HIGH;
   }

   public Dynamic2CommandExceptionType floatTooLow() {
      return FLOAT_TOO_LOW;
   }

   public Dynamic2CommandExceptionType floatTooHigh() {
      return FLOAT_TOO_HIGH;
   }

   public Dynamic2CommandExceptionType integerTooLow() {
      return INTEGER_TOO_LOW;
   }

   public Dynamic2CommandExceptionType integerTooHigh() {
      return INTEGER_TOO_HIGH;
   }

   public DynamicCommandExceptionType literalIncorrect() {
      return LITERAL_INCORRECT;
   }

   public SimpleCommandExceptionType readerExpectedStartOfQuote() {
      return READER_EXPECTED_START_OF_QUOTE;
   }

   public SimpleCommandExceptionType readerExpectedEndOfQuote() {
      return READER_EXPECTED_END_OF_QUOTE;
   }

   public DynamicCommandExceptionType readerInvalidEscape() {
      return READER_INVALID_ESCAPE;
   }

   public DynamicCommandExceptionType readerInvalidBool() {
      return READER_INVALID_BOOL;
   }

   public DynamicCommandExceptionType readerInvalidInt() {
      return READER_INVALID_INT;
   }

   public SimpleCommandExceptionType readerExpectedInt() {
      return READER_EXPECTED_INT;
   }

   public DynamicCommandExceptionType readerInvalidDouble() {
      return READER_INVALID_DOUBLE;
   }

   public SimpleCommandExceptionType readerExpectedDouble() {
      return READER_EXPECTED_DOUBLE;
   }

   public DynamicCommandExceptionType readerInvalidFloat() {
      return READER_INVALID_FLOAT;
   }

   public SimpleCommandExceptionType readerExpectedFloat() {
      return READER_EXPECTED_FLOAT;
   }

   public SimpleCommandExceptionType readerExpectedBool() {
      return READER_EXPECTED_BOOL;
   }

   public DynamicCommandExceptionType readerExpectedSymbol() {
      return READER_EXPECTED_SYMBOL;
   }

   public SimpleCommandExceptionType dispatcherUnknownCommand() {
      return DISPATCHER_UNKNOWN_COMMAND;
   }

   public SimpleCommandExceptionType dispatcherUnknownArgument() {
      return DISPATCHER_UNKNOWN_ARGUMENT;
   }

   public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
      return DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
   }

   public DynamicCommandExceptionType dispatcherParseException() {
      return DISPATCHER_PARSE_EXCEPTION;
   }
}