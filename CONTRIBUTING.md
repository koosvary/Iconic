# How to contribute
I'm really glad you're reading this.

## Merges into higher branches
This section is for merging your changes into the development branch (and later the master branch).
Mke sure you run every unit test in the suite before creating your pull request. We don't want any regressions. Once you've done this, you're good yo make your very own Pull Request!

### Creating a Pull Request (PR)
- Merges from any branch into *development* and *master* must go through a GitHub PR.
- Each PR must be assigned to me ([@scottwalkerau](https://github.com/scottwalkerau)) and have someone relevant as a reviewer. (Try not to add too many people as reviewers, at most 2)
- If you cannot think of someone relevant, leave it blank and I will do it myself or assign someone.
- Add any relevant comments for reviewers and myself on the PR. (There is a very small template for this)

### Reviewing a PR
- Read through every line changed so you understand **why** the PR is there.
- Flag any overall questions you have as a **Comment**
- Flag anything general you think should be done differently as **Request Changes** (For specific sections of the code, see below)
- If you think **every file** is good, select **Approve**

*NOTE:*
- You can select individual lines for single comments or starting a review.
- Selecting one of the three radio buttons applies the action to every file and doesn't reference a specific line.

## Coding conventions
I'm hoping you all adhere to these when writing your code. It will increase readability for your reviewers and maintainability later on...
- No single line *if*, *while*, *for*, etc. structures without curly braces. Use curly braces.
- Each method that does not call a subroutine should perform at most 1 function. (eg. A method to translate numbers to excel headers should not be burried within another function, it should be its own)
- If your methods are too long, create a private subroutine.
