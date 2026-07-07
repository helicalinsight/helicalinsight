
var isProd = process.env.NODE_ENV === 'production';
isProd = true;
var eslintConfig = {
    "extends": ["react-app", "react-app/jest"],
    "rules": isProd ? {} : {
        "no-unused-vars": ["error", { "vars": "all", "args": "after-used", "ignoreRestSiblings": true }]
    },
    "overrides": [
        {
            "files": ["**/*.stories.*"],
            "rules": {
                "import/no-anonymous-default-export": "off"
            }
        }
    ]
}

module.exports = eslintConfig
