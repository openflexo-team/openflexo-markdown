# Document With Code Blocks

This document exercises fenced and indented code block parsing.

## Java Example

The following is a fenced code block with language annotation:

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, OpenFlexo!");
    }
}
```

## Python Example

```python
def greet(name):
    return f"Hello, {name}!"

print(greet("OpenFlexo"))
```

## Shell Script

```bash
#!/bin/bash
echo "Building the project..."
./gradlew build
```

## Indented Code Block

An indented code block (4 spaces, no language hint):

    int x = 42;
    int y = x * 2;
    System.out.println(y);

## No-Language Fenced Block

```
Plain text inside a fenced block without language specification.
Line 2.
```

## After Code

Regular paragraph after the last code block.
