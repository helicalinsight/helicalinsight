import * as React from "react";

const ViewPlaceholderSVG = (props) => (
    <svg
        role="img"
        width={700}
        height={400}
        aria-labelledby="loading-aria"
        viewBox="0 0 700 400"
        preserveAspectRatio="none"
        {...props}
    >
        <title id="loading-aria">{"Loading..."}</title>
        <rect
            x={0}
            y={0}
            width="100%"
            height="100%"
            clipPath="url(#clip-path)"
            style={{
                fill: 'url("#fill")',
            }}
        />
        <defs>
            <clipPath id="clip-path">
                <rect x={462} y={92} rx={0} ry={0} width={11} height={17} />
                <rect x={462} y={123} rx={0} ry={0} width={11} height={17} />
                <rect x={462} y={187} rx={0} ry={0} width={11} height={17} />
                <rect x={462} y={156} rx={0} ry={0} width={11} height={17} />
                <rect x={462} y={219} rx={0} ry={0} width={11} height={17} />
                <rect x={17} y={94} rx={0} ry={0} width={431} height={146} />
                <rect x={326} y={102} rx={0} ry={0} width={84} height={4} />
                <rect x={456} y={55} rx={5} ry={5} width={139} height={24} />
                <rect x={56} y={63} rx={4} ry={4} width={87} height={24} />
                <rect x={485} y={92} rx={0} ry={0} width={112} height={17} />
                <rect x={485} y={123} rx={0} ry={0} width={112} height={17} />
                <rect x={485} y={187} rx={0} ry={0} width={112} height={17} />
                <rect x={485} y={156} rx={0} ry={0} width={112} height={17} />
                <rect x={485} y={219} rx={0} ry={0} width={112} height={17} />
                <circle cx={30} cy={73} r={10} />
                <rect x={210} y={59} rx={4} ry={4} width={87} height={24} />
                <circle cx={184} cy={71} r={10} />
                <rect x={139} y={19} rx={4} ry={4} width={455} height={24} />
                <rect x={21} y={20} rx={4} ry={4} width={87} height={24} />
                <rect x={20} y={260} rx={0} ry={0} width={581} height={31} />
                <rect x={21} y={312} rx={0} ry={0} width={581} height={31} />
                <rect x={23} y={368} rx={0} ry={0} width={581} height={31} />
            </clipPath>
            <linearGradient id="fill">
                <stop offset={0.599964} stopColor="#f3f3f3" stopOpacity={1}>
                    <animate
                        attributeName="offset"
                        values="-2; -2; 1"
                        keyTimes="0; 0.25; 1"
                        dur="2s"
                        repeatCount="indefinite"
                    />
                </stop>
                <stop offset={1.59996} stopColor="#e9e2e2" stopOpacity={1}>
                    <animate
                        attributeName="offset"
                        values="-1; -1; 2"
                        keyTimes="0; 0.25; 1"
                        dur="2s"
                        repeatCount="indefinite"
                    />
                </stop>
                <stop offset={2.59996} stopColor="#f3f3f3" stopOpacity={1}>
                    <animate
                        attributeName="offset"
                        values="0; 0; 3"
                        keyTimes="0; 0.25; 1"
                        dur="2s"
                        repeatCount="indefinite"
                    />
                </stop>
            </linearGradient>
        </defs>
    </svg>


);

export default ViewPlaceholderSVG;
