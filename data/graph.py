import matplotlib.pyplot as plt
import numpy as np
label = ['Player 1', 'Player 2']
no_wins = [100, 0]
index = np.arange(len(label))
print(index)
plt.bar(index, no_wins)
plt.xlabel('Player', fontsize=12)
plt.ylabel('No of Wins', fontsize=12)
plt.xticks(index, label, fontsize=12)
plt.title('Win Ratio of AI over 100 Games')
plt.show()

column1 = []
column2 = []
with open('experiment1', 'r') as file1:
    for line in file1:
        line_list = line.split();
        column1.append(int(line_list[0]))
        column2.append(float(line_list[1]))

index = np.arange(100)
plt.plot(index, column2, 'b-')
plt.xlabel('Number of Games', fontsize=12)
plt.ylabel('Average Computation per Turn (ms)', fontsize=12)
plt.axis([0, 100, 0, 1000])
plt.title('Average Efficieny of AI per Game')
plt.show();

