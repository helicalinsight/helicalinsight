export const checkParanthesis = (str) => {
    var stack = [];
    var brace_pairings = { ")": "(", "}": "{", "]": "[" };
    for (var i = 0; i < str.length; i++) {
        if (str[i] == "(" || str[i] == "{" || str[i] == "[") {
            stack.push(str[i]);
        } else if (str[i] in brace_pairings) {
            if (stack.pop() != brace_pairings[str[i]]) { return false; }
        }
    }

    return !stack.length;
}