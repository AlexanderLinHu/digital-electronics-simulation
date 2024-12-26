% cspell:disable
% Original file comes from https://github.com/gillescastel/masterthesis/blob/master/thesis/tuftebook.cls
\NeedsTeXFormat{LaTeX2e}

\LoadClassWithOptions{book}
\RequirePackage{microtype}
\RequirePackage{geometry}
\geometry{margin=2cm}


\RequirePackage{titlesec,titletoc}
\RequirePackage{fmtcount}
\titleformat{\chapter}[display]
  {\bfseries\Large\sf}
  {\filleft\textls{CHAPTER} \expandafter\textls\expandafter{\NUMBERstring{chapter}}}
  {1.5ex}
  {\titlerule\vspace*{1.1ex}\filright}
  [\vspace*{1.5ex}\titlerule]


%set up header, footer and page numbering
\RequirePackage{fancyhdr}% helper package
\fancypagestyle{normal}{
    \fancyhf{}%
    \fancyhead[OR]{\rightmark}
    \fancyhead[OL]{\thepage}
    \fancyhead[ER]{\thepage}
    \fancyhead[EL]{\leftmark}
    \renewcommand{\headrulewidth}{0.4pt}% Line at the header visible
}

% This is used for chapters
\fancypagestyle{plain}{%
  \fancyhf{}%
    \renewcommand{\headrulewidth}{0pt}% Line at the header visible
}

\renewcommand{\normalsize}{\fontsize{10pt}{13pt}\selectfont}%
\renewcommand{\footnotesize}{\fontsize{8pt}{10pt}\selectfont}%
