I want to rewrite some bits of the compiler.

Here's how it should run when you compile a module.

- The preprocessor includes whatever standard libraries.
- We type check everything.
- We determine which functions can be inlined, by looking through their function dependencies.
- We compile all of those inlineable functions to intermediate code. We make a map out of them.
- We compile all of the other functions, giving them as an argument the map of inlineable functions.

Ways we can make a counter work:

- Every node in the AST has a pointer to its parent. The unique counter value is a list of all those positions.
- We pass in a list of parent positions as an argument to toIntermediate
- We pass a number into toIntermediate, and it increments it however many times, and we return what we're up to.

The global variable version of Counter worked badly because I wanted to swap out different versions of compiled code, and they had non-matching variable names.

Other things to rewrite:

- In AssemblyMaker, I should use mutating methods of List.

